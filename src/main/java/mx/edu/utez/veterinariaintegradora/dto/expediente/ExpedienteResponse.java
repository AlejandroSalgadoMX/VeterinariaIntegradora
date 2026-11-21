package mx.edu.utez.veterinariaintegradora.dto.expediente;

import mx.edu.utez.veterinariaintegradora.dto.cobro.PagoResponse;
import mx.edu.utez.veterinariaintegradora.dto.consulta.CitaAtencionResponse;
import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialResponse;
import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaResponse;
import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** HU-VET-04 · Expediente completo de la mascota (solo consulta). */
public record ExpedienteResponse(
        MascotaResponse mascota,
        DuenoContacto dueno,
        List<UltimaAplicacion> ultimasAplicaciones,
        List<Visita> visitas
) {

    public record DuenoContacto(Long id, String nombreCompleto, String telefono, String correo) {
    }

    public record UltimaAplicacion(
            Long atencionId,
            String atencionNombre,
            TipoAtencion tipo,
            LocalDateTime ultimaAplicacion,
            Integer intervaloMinimoDias,
            LocalDate proximaAplicacionPermitida
    ) {
    }

    public record Visita(
            Long citaId,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            String motivo,
            EstadoCita estado,
            String veterinarioNombre,
            HistorialResponse notaClinica,
            List<CitaAtencionResponse> atenciones,
            Long cargoId,
            BigDecimal totalCargo,
            PagoResponse pago
    ) {
    }
}
