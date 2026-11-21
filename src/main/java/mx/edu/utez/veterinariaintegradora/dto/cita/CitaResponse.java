package mx.edu.utez.veterinariaintegradora.dto.cita;

import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioResponse;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long mascotaId,
        String mascotaNombre,
        String especieNombre,
        Long duenoId,
        String duenoNombre,
        String duenoTelefono,
        Long veterinarioId,
        String veterinarioNombre,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String motivo,
        EstadoCita estado,
        LocalDateTime creadaEn
) {

    public static CitaResponse from(Cita c) {
        return new CitaResponse(
                c.getId(),
                c.getMascota().getId(),
                c.getMascota().getNombre(),
                c.getMascota().getEspecie().getNombre(),
                c.getMascota().getDueno().getId(),
                DuenoResponse.nombreCompleto(c.getMascota().getDueno()),
                c.getMascota().getDueno().getTelefono(),
                c.getVeterinario().getId(),
                VeterinarioResponse.nombreCompleto(c.getVeterinario()),
                c.getFechaHoraInicio(),
                c.getFechaHoraFin(),
                c.getMotivo(),
                c.getEstado(),
                c.getCreadaEn()
        );
    }
}
