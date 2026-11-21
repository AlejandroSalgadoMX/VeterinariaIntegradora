package mx.edu.utez.veterinariaintegradora.service.expediente;

import mx.edu.utez.veterinariaintegradora.dto.cobro.PagoResponse;
import mx.edu.utez.veterinariaintegradora.dto.consulta.CitaAtencionResponse;
import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialResponse;
import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.dto.expediente.ExpedienteResponse;
import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaResponse;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioResponse;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cobro.Cargo;
import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaAtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.HistorialClinicoRepository;
import mx.edu.utez.veterinariaintegradora.repository.cobro.CargoRepository;
import mx.edu.utez.veterinariaintegradora.repository.cobro.PagoRepository;
import mx.edu.utez.veterinariaintegradora.service.cobro.CobroService;
import mx.edu.utez.veterinariaintegradora.service.consulta.AtencionClinicaService;
import mx.edu.utez.veterinariaintegradora.service.mascota.MascotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-VET-04 · Consulta de expediente: notas, atenciones (con fechas útiles para intervalos),
 * insumos usados y cargos asociados. Solo lectura.
 */
@Service
public class ExpedienteService {

    private final MascotaService mascotaService;
    private final CitaRepository citaRepository;
    private final HistorialClinicoRepository historialRepository;
    private final CitaAtencionRepository citaAtencionRepository;
    private final CargoRepository cargoRepository;
    private final PagoRepository pagoRepository;
    private final AtencionClinicaService atencionClinicaService;
    private final CobroService cobroService;

    public ExpedienteService(MascotaService mascotaService,
                             CitaRepository citaRepository,
                             HistorialClinicoRepository historialRepository,
                             CitaAtencionRepository citaAtencionRepository,
                             CargoRepository cargoRepository,
                             PagoRepository pagoRepository,
                             AtencionClinicaService atencionClinicaService,
                             CobroService cobroService) {
        this.mascotaService = mascotaService;
        this.citaRepository = citaRepository;
        this.historialRepository = historialRepository;
        this.citaAtencionRepository = citaAtencionRepository;
        this.cargoRepository = cargoRepository;
        this.pagoRepository = pagoRepository;
        this.atencionClinicaService = atencionClinicaService;
        this.cobroService = cobroService;
    }

    @Transactional(readOnly = true)
    public ExpedienteResponse obtener(Long mascotaId) {
        Mascota mascota = mascotaService.buscar(mascotaId);
        Dueno dueno = mascota.getDueno();

        List<ExpedienteResponse.Visita> visitas = citaRepository.findByMascota_IdOrderByFechaHoraInicioDesc(mascotaId)
                .stream()
                .map(this::toVisita)
                .toList();

        return new ExpedienteResponse(
                MascotaResponse.from(mascota),
                new ExpedienteResponse.DuenoContacto(dueno.getId(), DuenoResponse.nombreCompleto(dueno),
                        dueno.getTelefono(), dueno.getCorreo()),
                ultimasAplicaciones(visitas),
                visitas
        );
    }

    private ExpedienteResponse.Visita toVisita(Cita cita) {
        HistorialResponse nota = historialRepository.findByCita_Id(cita.getId())
                .map(HistorialResponse::from)
                .orElse(null);
        List<CitaAtencionResponse> atenciones = citaAtencionRepository.findByCita_IdOrderByAplicadaEnAsc(cita.getId())
                .stream()
                .map(atencionClinicaService::toResponse)
                .toList();
        Cargo cargo = cargoRepository.findByCita_Id(cita.getId()).orElse(null);
        BigDecimal total = cargo != null ? cobroService.calcularTotal(cita.getId()) : null;
        PagoResponse pago = cargo != null
                ? pagoRepository.findByCargo_Id(cargo.getId()).map(PagoResponse::from).orElse(null)
                : null;
        return new ExpedienteResponse.Visita(
                cita.getId(),
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFin(),
                cita.getMotivo(),
                cita.getEstado(),
                VeterinarioResponse.nombreCompleto(cita.getVeterinario()),
                nota,
                atenciones,
                cargo != null ? cargo.getId() : null,
                total,
                pago
        );
    }

    /** Resume, por atención del catálogo, la última aplicación y desde cuándo puede repetirse. */
    private List<ExpedienteResponse.UltimaAplicacion> ultimasAplicaciones(List<ExpedienteResponse.Visita> visitas) {
        Map<Long, CitaAtencionResponse> ultimas = new LinkedHashMap<>();
        visitas.stream()
                .flatMap(v -> v.atenciones().stream())
                .forEach(a -> ultimas.merge(a.atencionId(), a,
                        (x, y) -> x.aplicadaEn().isAfter(y.aplicadaEn()) ? x : y));
        return ultimas.values().stream()
                .sorted(Comparator.comparing(CitaAtencionResponse::aplicadaEn).reversed())
                .map(a -> {
                    Integer intervalo = atencionClinicaService.intervaloDe(a.atencionId());
                    return new ExpedienteResponse.UltimaAplicacion(
                            a.atencionId(),
                            a.atencionNombre(),
                            a.tipo(),
                            a.aplicadaEn(),
                            intervalo,
                            intervalo != null ? a.aplicadaEn().toLocalDate().plusDays(intervalo) : null);
                })
                .toList();
    }
}
