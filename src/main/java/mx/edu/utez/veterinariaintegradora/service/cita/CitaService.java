package mx.edu.utez.veterinariaintegradora.service.cita;

import mx.edu.utez.veterinariaintegradora.dto.cita.CitaRequest;
import mx.edu.utez.veterinariaintegradora.dto.cita.CitaResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaAtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.cita.CitaRepository;
import mx.edu.utez.veterinariaintegradora.repository.mascota.MascotaRepository;
import mx.edu.utez.veterinariaintegradora.repository.veterinario.VeterinarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * HU-REC-03 · Agenda de citas.
 * Reglas: sin traslapes para el mismo veterinario, veterinario inactivo no recibe citas nuevas.
 * Flujo de estados: PROGRAMADA -> EN_ATENCION -> POR_COBRAR (al finalizar) -> CERRADA (al pagar);
 * PROGRAMADA -> CANCELADA / NO_ASISTIO.
 */
@Service
public class CitaService {

    /** Estados que liberan el horario y no cuentan para traslapes. */
    public static final Set<EstadoCita> ESTADOS_SIN_ATENCION = EnumSet.of(EstadoCita.CANCELADA, EstadoCita.NO_ASISTIO);

    private static final long SIN_CITA = 0L;

    private final CitaRepository citaRepository;
    private final CitaAtencionRepository citaAtencionRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public CitaService(CitaRepository citaRepository,
                       CitaAtencionRepository citaAtencionRepository,
                       MascotaRepository mascotaRepository,
                       VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.citaAtencionRepository = citaAtencionRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listar(LocalDate fecha, Long veterinarioId, Long mascotaId, EstadoCita estado) {
        List<Cita> citas = fecha != null
                ? citaRepository.findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(
                        fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay().minusNanos(1))
                : citaRepository.findAll(Sort.by("fechaHoraInicio"));
        return citas.stream()
                .filter(c -> veterinarioId == null || c.getVeterinario().getId().equals(veterinarioId))
                .filter(c -> mascotaId == null || c.getMascota().getId().equals(mascotaId))
                .filter(c -> estado == null || c.getEstado() == estado)
                .map(CitaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CitaResponse obtener(Long id) {
        return CitaResponse.from(buscar(id));
    }

    @Transactional
    public CitaResponse agendar(CitaRequest request) {
        Cita cita = new Cita();
        asignarDatos(cita, request, SIN_CITA);
        cita.setEstado(EstadoCita.PROGRAMADA);
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponse reprogramar(Long id, CitaRequest request) {
        Cita cita = buscar(id);
        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new ReglaNegocioException("Solo se pueden modificar citas en estado PROGRAMADA.");
        }
        asignarDatos(cita, request, id);
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponse cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = buscar(id);
        EstadoCita actual = cita.getEstado();
        switch (nuevoEstado) {
            case EN_ATENCION -> {
                validarEstado(actual, EstadoCita.PROGRAMADA, nuevoEstado);
                if (!cita.getVeterinario().isActivo()) {
                    throw new ReglaNegocioException("El veterinario asignado está inactivo; reasigna la cita.");
                }
            }
            case NO_ASISTIO -> validarEstado(actual, EstadoCita.PROGRAMADA, nuevoEstado);
            case CANCELADA -> {
                if (actual != EstadoCita.PROGRAMADA && actual != EstadoCita.EN_ATENCION) {
                    throw new ReglaNegocioException("Solo se pueden cancelar citas PROGRAMADAS o EN_ATENCION.");
                }
                if (citaAtencionRepository.existsByCita_Id(id)) {
                    throw new ReglaNegocioException(
                            "La cita ya tiene atenciones registradas; elimínalas antes de cancelarla.");
                }
            }
            case POR_COBRAR -> throw new ReglaNegocioException(
                    "La cita pasa a POR_COBRAR al finalizar la atención (POST /api/citas/{id}/finalizar).");
            case CERRADA -> throw new ReglaNegocioException(
                    "La cita pasa a CERRADA al registrar el pago (POST /api/citas/{id}/pago).");
            case PROGRAMADA -> throw new ReglaNegocioException("Una cita no puede regresar a PROGRAMADA.");
        }
        cita.setEstado(nuevoEstado);
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional(readOnly = true)
    public Cita buscar(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", id));
    }

    private void asignarDatos(Cita cita, CitaRequest request, Long citaExcluida) {
        if (!request.getFechaHoraFin().isAfter(request.getFechaHoraInicio())) {
            throw new ReglaNegocioException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota", request.getMascotaId()));
        if (mascota.getEstado() != EstadoMascota.ACTIVA) {
            throw new ReglaNegocioException("Solo se agendan citas para mascotas ACTIVAS (estado actual: "
                    + mascota.getEstado() + ").");
        }
        Veterinario veterinario = veterinarioRepository.findByIdParaAgendar(request.getVeterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", request.getVeterinarioId()));
        if (!veterinario.isActivo() || !veterinario.getUsuario().isActivo()) {
            throw new ReglaNegocioException("El veterinario está inactivo y no puede recibir citas nuevas.");
        }
        LocalDateTime inicio = request.getFechaHoraInicio();
        LocalDateTime fin = request.getFechaHoraFin();
        if (citaRepository.existeTraslapeVeterinario(veterinario.getId(), inicio, fin, citaExcluida, ESTADOS_SIN_ATENCION)) {
            throw new ReglaNegocioException("El veterinario ya tiene una cita que se traslapa con ese horario.");
        }
        if (citaRepository.existeTraslapeMascota(mascota.getId(), inicio, fin, citaExcluida, ESTADOS_SIN_ATENCION)) {
            throw new ReglaNegocioException("La mascota ya tiene otra cita que se traslapa con ese horario.");
        }
        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        cita.setFechaHoraInicio(inicio);
        cita.setFechaHoraFin(fin);
        cita.setMotivo(request.getMotivo().trim());
    }

    private void validarEstado(EstadoCita actual, EstadoCita requerido, EstadoCita nuevo) {
        if (actual != requerido) {
            throw new ReglaNegocioException("No se puede pasar de " + actual + " a " + nuevo
                    + "; la cita debe estar en " + requerido + ".");
        }
    }
}
