package mx.edu.utez.veterinariaintegradora.service.consulta;

import mx.edu.utez.veterinariaintegradora.dto.consulta.AtencionAplicadaRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.CitaAtencionResponse;
import mx.edu.utez.veterinariaintegradora.dto.consulta.LoteSeleccionRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.UsoInsumoResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.atencion.Atencion;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumo;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import mx.edu.utez.veterinariaintegradora.model.inventario.LoteInsumo;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionEspecieRepository;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionInsumoRepository;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaAtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.UsoInsumoRepository;
import mx.edu.utez.veterinariaintegradora.service.cita.CitaService;
import mx.edu.utez.veterinariaintegradora.service.inventario.InventarioService;
import mx.edu.utez.veterinariaintegradora.service.mascota.EdadMascota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-VET-02 · Atenciones aplicadas.
 * Antes de registrar se valida especie permitida, edad mínima, intervalo desde la última aplicación
 * y, si la atención exige insumos, que exista un lote vigente con existencia para descontarlo.
 * Todo ocurre en una sola transacción: si algo no cuadra, no se registra nada.
 */
@Service
public class AtencionClinicaService {

    private final CitaService citaService;
    private final InventarioService inventarioService;
    private final AtencionRepository atencionRepository;
    private final AtencionEspecieRepository atencionEspecieRepository;
    private final AtencionInsumoRepository atencionInsumoRepository;
    private final CitaAtencionRepository citaAtencionRepository;
    private final UsoInsumoRepository usoInsumoRepository;

    public AtencionClinicaService(CitaService citaService,
                                  InventarioService inventarioService,
                                  AtencionRepository atencionRepository,
                                  AtencionEspecieRepository atencionEspecieRepository,
                                  AtencionInsumoRepository atencionInsumoRepository,
                                  CitaAtencionRepository citaAtencionRepository,
                                  UsoInsumoRepository usoInsumoRepository) {
        this.citaService = citaService;
        this.inventarioService = inventarioService;
        this.atencionRepository = atencionRepository;
        this.atencionEspecieRepository = atencionEspecieRepository;
        this.atencionInsumoRepository = atencionInsumoRepository;
        this.citaAtencionRepository = citaAtencionRepository;
        this.usoInsumoRepository = usoInsumoRepository;
    }

    @Transactional(readOnly = true)
    public List<CitaAtencionResponse> listar(Long citaId) {
        citaService.buscar(citaId);
        return citaAtencionRepository.findByCita_IdOrderByAplicadaEnAsc(citaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CitaAtencionResponse registrar(Long citaId, AtencionAplicadaRequest request) {
        Cita cita = citaService.buscar(citaId);
        validarCitaEnAtencion(cita);
        Mascota mascota = cita.getMascota();
        Atencion atencion = atencionRepository.findById(request.getAtencionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Atención", request.getAtencionId()));
        LocalDate hoy = LocalDate.now();

        validarAtencionActiva(atencion);
        validarEspecie(atencion, mascota);
        validarEdadMinima(atencion, mascota, hoy);
        validarIntervalo(atencion, mascota, hoy);

        // Se reservan (bloquean) los lotes de los insumos obligatorios antes de guardar nada.
        Map<Long, Long> lotesElegidos = indexarLotes(request.getLotes());
        List<AtencionInsumo> requeridos = atencionInsumoRepository.findByAtencion_Id(atencion.getId());
        List<UsoInsumo> usos = new ArrayList<>();
        for (AtencionInsumo requerido : requeridos) {
            BigDecimal cantidad = requerido.getCantidadRequerida();
            Long loteElegido = lotesElegidos.remove(requerido.getInsumo().getId());
            LoteInsumo lote = loteElegido != null
                    ? inventarioService.reservarLote(loteElegido, requerido.getInsumo().getId(), cantidad)
                    : inventarioService.reservarLoteVigente(requerido.getInsumo(), cantidad);
            UsoInsumo uso = new UsoInsumo();
            uso.setLoteInsumo(lote);
            uso.setCantidadUsada(cantidad);
            // El insumo obligatorio ya va incluido en el precio de la atención.
            uso.setPrecioCobradoUnitario(BigDecimal.ZERO);
            usos.add(uso);
        }
        if (!lotesElegidos.isEmpty()) {
            throw new ReglaNegocioException("Los insumos " + lotesElegidos.keySet()
                    + " no son obligatorios para esta atención; regístralos como uso de insumo adicional.");
        }

        CitaAtencion citaAtencion = new CitaAtencion();
        citaAtencion.setCita(cita);
        citaAtencion.setAtencion(atencion);
        citaAtencion.setPrecioAplicado(atencion.getPrecioBase());
        citaAtencion.setObservaciones(request.getObservaciones() == null || request.getObservaciones().isBlank()
                ? null : request.getObservaciones().trim());
        citaAtencion = citaAtencionRepository.save(citaAtencion);

        for (UsoInsumo uso : usos) {
            uso.setCitaAtencion(citaAtencion);
        }
        usoInsumoRepository.saveAll(usos);
        return toResponse(citaAtencion);
    }

    /** Permite corregir una atención registrada por error mientras la cita sigue EN_ATENCION. */
    @Transactional
    public void eliminar(Long citaId, Long citaAtencionId) {
        Cita cita = citaService.buscar(citaId);
        validarCitaEnAtencion(cita);
        CitaAtencion citaAtencion = buscarDeCita(citaId, citaAtencionId);
        usoInsumoRepository.deleteAll(usoInsumoRepository.findByCitaAtencion_IdOrderByUsadoEnAsc(citaAtencionId));
        citaAtencionRepository.delete(citaAtencion);
    }

    @Transactional(readOnly = true)
    public CitaAtencion buscarDeCita(Long citaId, Long citaAtencionId) {
        CitaAtencion citaAtencion = citaAtencionRepository.findById(citaAtencionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atención aplicada", citaAtencionId));
        if (!citaAtencion.getCita().getId().equals(citaId)) {
            throw new ReglaNegocioException("La atención aplicada " + citaAtencionId + " no pertenece a la cita " + citaId + ".");
        }
        return citaAtencion;
    }

    public void validarCitaEnAtencion(Cita cita) {
        if (cita.getEstado() != EstadoCita.EN_ATENCION) {
            throw new ReglaNegocioException("La cita debe estar EN_ATENCION para documentar la visita (estado actual: "
                    + cita.getEstado() + ").");
        }
    }

    public CitaAtencionResponse toResponse(CitaAtencion ca) {
        List<UsoInsumoResponse> usos = usoInsumoRepository.findByCitaAtencion_IdOrderByUsadoEnAsc(ca.getId()).stream()
                .map(UsoInsumoResponse::from)
                .toList();
        return CitaAtencionResponse.from(ca, usos);
    }

    // ===== Validaciones del DFR =====

    private void validarAtencionActiva(Atencion atencion) {
        if (!atencion.isActiva()) {
            throw new ReglaNegocioException("La atención " + atencion.getNombre() + " está inactiva en el catálogo.");
        }
    }

    private void validarEspecie(Atencion atencion, Mascota mascota) {
        if (!atencionEspecieRepository.existsByAtencion_IdAndEspecie_Id(atencion.getId(), mascota.getEspecie().getId())) {
            throw new ReglaNegocioException("La atención " + atencion.getNombre() + " no aplica para la especie "
                    + mascota.getEspecie().getNombre() + ".");
        }
    }

    private void validarEdadMinima(Atencion atencion, Mascota mascota, LocalDate hoy) {
        if (atencion.getEdadMinimaDias() == null) {
            return;
        }
        long edad = EdadMascota.enDias(mascota, hoy);
        if (edad < atencion.getEdadMinimaDias()) {
            throw new ReglaNegocioException(mascota.getNombre() + " tiene " + edad + " días de edad y "
                    + atencion.getNombre() + " requiere al menos " + atencion.getEdadMinimaDias() + " días.");
        }
    }

    private void validarIntervalo(Atencion atencion, Mascota mascota, LocalDate hoy) {
        if (atencion.getIntervaloMinimoDias() == null) {
            return;
        }
        LocalDateTime ultima = citaAtencionRepository.buscarUltimaAplicacion(
                atencion.getId(), mascota.getId(), CitaService.ESTADOS_SIN_ATENCION);
        if (ultima == null) {
            return;
        }
        long transcurridos = ChronoUnit.DAYS.between(ultima.toLocalDate(), hoy);
        if (transcurridos < atencion.getIntervaloMinimoDias()) {
            LocalDate siguiente = ultima.toLocalDate().plusDays(atencion.getIntervaloMinimoDias());
            throw new ReglaNegocioException("Aún no toca aplicar " + atencion.getNombre() + " a " + mascota.getNombre()
                    + ": la última aplicación fue el " + ultima.toLocalDate() + " y la siguiente es a partir del "
                    + siguiente + ".");
        }
    }

    private Map<Long, Long> indexarLotes(List<LoteSeleccionRequest> lotes) {
        Map<Long, Long> indice = new HashMap<>();
        if (lotes == null) {
            return indice;
        }
        for (LoteSeleccionRequest seleccion : lotes) {
            if (indice.put(seleccion.getInsumoId(), seleccion.getLoteInsumoId()) != null) {
                throw new ReglaNegocioException("El insumo " + seleccion.getInsumoId() + " tiene más de un lote seleccionado.");
            }
        }
        return indice;
    }
}
