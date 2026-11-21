package mx.edu.utez.veterinariaintegradora.service.cobro;

import mx.edu.utez.veterinariaintegradora.dto.cobro.CargoResponse;
import mx.edu.utez.veterinariaintegradora.dto.cobro.CitaPorCobrarResponse;
import mx.edu.utez.veterinariaintegradora.dto.cobro.ConceptoCargoResponse;
import mx.edu.utez.veterinariaintegradora.dto.cobro.PagoRequest;
import mx.edu.utez.veterinariaintegradora.dto.cobro.PagoResponse;
import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import mx.edu.utez.veterinariaintegradora.model.cobro.Cargo;
import mx.edu.utez.veterinariaintegradora.model.cobro.Pago;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaAtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.UsoInsumoRepository;
import mx.edu.utez.veterinariaintegradora.repository.cobro.CargoRepository;
import mx.edu.utez.veterinariaintegradora.repository.cobro.PagoRepository;
import mx.edu.utez.veterinariaintegradora.service.cita.CitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Arma el cargo de la cita a partir de las atenciones aplicadas y los insumos cobrables
 * (igual que la vista vw_total_cargo). Al documentar la visita la cita queda POR_COBRAR.
 */
@Service
public class CobroService {

    private final CitaService citaService;
    private final CitaRepository citaRepository;
    private final CitaAtencionRepository citaAtencionRepository;
    private final UsoInsumoRepository usoInsumoRepository;
    private final CargoRepository cargoRepository;
    private final PagoRepository pagoRepository;

    public CobroService(CitaService citaService,
                        CitaRepository citaRepository,
                        CitaAtencionRepository citaAtencionRepository,
                        UsoInsumoRepository usoInsumoRepository,
                        CargoRepository cargoRepository,
                        PagoRepository pagoRepository) {
        this.citaService = citaService;
        this.citaRepository = citaRepository;
        this.citaAtencionRepository = citaAtencionRepository;
        this.usoInsumoRepository = usoInsumoRepository;
        this.cargoRepository = cargoRepository;
        this.pagoRepository = pagoRepository;
    }

    /** HU-VET-02: al terminar de documentar la visita se genera el cargo y la cita queda POR_COBRAR. */
    @Transactional
    public CargoResponse finalizarAtencion(Long citaId) {
        Cita cita = citaService.buscar(citaId);
        if (cita.getEstado() != EstadoCita.EN_ATENCION) {
            throw new ReglaNegocioException("Solo se puede finalizar una cita EN_ATENCION (estado actual: "
                    + cita.getEstado() + ").");
        }
        if (!citaAtencionRepository.existsByCita_Id(citaId)) {
            throw new ReglaNegocioException("Registra al menos una atención antes de finalizar la visita.");
        }
        if (cargoRepository.existsByCita_Id(citaId)) {
            throw new ReglaNegocioException("La cita ya tiene un cargo generado.");
        }
        Cargo cargo = new Cargo();
        cargo.setCita(cita);
        cargo = cargoRepository.save(cargo);

        cita.setEstado(EstadoCita.POR_COBRAR);
        citaRepository.save(cita);
        return construirRespuesta(cita, cargo, null);
    }

    @Transactional(readOnly = true)
    public CargoResponse obtenerCargo(Long citaId) {
        Cita cita = citaService.buscar(citaId);
        Cargo cargo = cargoRepository.findByCita_Id(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cargo de la cita", citaId));
        Pago pago = pagoRepository.findByCargo_Id(cargo.getId()).orElse(null);
        return construirRespuesta(cita, cargo, pago);
    }

    /** HU-REC-04: lista de citas atendidas pendientes de pago. */
    @Transactional(readOnly = true)
    public List<CitaPorCobrarResponse> listarPendientes() {
        return citaRepository.findByEstadoOrderByFechaHoraInicioAsc(EstadoCita.POR_COBRAR).stream()
                .map(cita -> new CitaPorCobrarResponse(
                        cita.getId(),
                        cargoRepository.findByCita_Id(cita.getId()).map(Cargo::getId).orElse(null),
                        cita.getMascota().getNombre(),
                        DuenoResponse.nombreCompleto(cita.getMascota().getDueno()),
                        cita.getMascota().getDueno().getTelefono(),
                        VeterinarioResponse.nombreCompleto(cita.getVeterinario()),
                        cita.getFechaHoraInicio(),
                        calcularTotal(cita.getId())))
                .toList();
    }

    /**
     * HU-REC-04: un solo pago por el importe completo del cargo (sin pagos parciales).
     * Al registrarlo, la cita pasa a CERRADA.
     */
    @Transactional
    public CargoResponse registrarPago(Long citaId, PagoRequest request) {
        Cita cita = citaService.buscar(citaId);
        if (cita.getEstado() == EstadoCita.CERRADA) {
            throw new ReglaNegocioException("La cita ya está CERRADA; su cargo ya fue pagado.");
        }
        if (cita.getEstado() != EstadoCita.POR_COBRAR) {
            throw new ReglaNegocioException("Solo se cobran citas POR_COBRAR (estado actual: " + cita.getEstado() + ").");
        }
        Cargo cargo = cargoRepository.findByCita_Id(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cargo de la cita", citaId));
        if (pagoRepository.existsByCargo_Id(cargo.getId())) {
            throw new ReglaNegocioException("El cargo de esta cita ya tiene un pago registrado.");
        }
        BigDecimal total = calcularTotal(citaId);
        if (request.getMontoPagado().compareTo(total) != 0) {
            throw new ReglaNegocioException("No se permiten pagos parciales ni montos distintos: el total del cargo es $"
                    + total + " y se recibió $" + request.getMontoPagado() + ".");
        }
        Pago pago = new Pago();
        pago.setCargo(cargo);
        pago.setFormaPago(request.getFormaPago());
        pago.setMontoPagado(total);
        pago = pagoRepository.save(pago);

        cita.setEstado(EstadoCita.CERRADA);
        citaRepository.save(cita);
        return construirRespuesta(cita, cargo, pago);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Long citaId) {
        return conceptos(citaId).stream()
                .map(ConceptoCargoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    protected CargoResponse construirRespuesta(Cita cita, Cargo cargo, Pago pago) {
        List<ConceptoCargoResponse> conceptos = conceptos(cita.getId());
        BigDecimal total = conceptos.stream()
                .map(ConceptoCargoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return new CargoResponse(
                cargo.getId(),
                cita.getId(),
                cita.getEstado(),
                cita.getMascota().getId(),
                cita.getMascota().getNombre(),
                DuenoResponse.nombreCompleto(cita.getMascota().getDueno()),
                cargo.getGeneradoEn(),
                conceptos,
                total,
                pago != null,
                pago != null ? PagoResponse.from(pago) : null
        );
    }

    private List<ConceptoCargoResponse> conceptos(Long citaId) {
        List<ConceptoCargoResponse> conceptos = new ArrayList<>();
        for (CitaAtencion ca : citaAtencionRepository.findByCita_IdOrderByAplicadaEnAsc(citaId)) {
            conceptos.add(new ConceptoCargoResponse("ATENCION", ca.getAtencion().getNombre(),
                    BigDecimal.ONE, ca.getPrecioAplicado(), ca.getPrecioAplicado()));
        }
        for (UsoInsumo uso : usoInsumoRepository.findByCitaAtencion_Cita_IdOrderByUsadoEnAsc(citaId)) {
            if (uso.getPrecioCobradoUnitario().signum() == 0) {
                continue;
            }
            BigDecimal subtotal = uso.getCantidadUsada().multiply(uso.getPrecioCobradoUnitario())
                    .setScale(2, RoundingMode.HALF_UP);
            conceptos.add(new ConceptoCargoResponse("INSUMO",
                    uso.getLoteInsumo().getInsumo().getNombre() + " (lote " + uso.getLoteInsumo().getCodigoLote() + ")",
                    uso.getCantidadUsada(), uso.getPrecioCobradoUnitario(), subtotal));
        }
        return conceptos;
    }
}
