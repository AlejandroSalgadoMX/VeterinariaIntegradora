package mx.edu.utez.veterinariaintegradora.dto.mascota;

import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.model.mascota.Sexo;
import mx.edu.utez.veterinariaintegradora.service.mascota.EdadMascota;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MascotaResponse(
        Long id,
        Long duenoId,
        String duenoNombre,
        Integer especieId,
        String especieNombre,
        String nombre,
        String raza,
        Sexo sexo,
        LocalDate fechaNacimiento,
        Integer edadAproximadaMeses,
        long edadDias,
        String senasParticulares,
        EstadoMascota estado,
        LocalDateTime creadoEn
) {

    public static MascotaResponse from(Mascota m) {
        return new MascotaResponse(
                m.getId(),
                m.getDueno().getId(),
                DuenoResponse.nombreCompleto(m.getDueno()),
                m.getEspecie().getId(),
                m.getEspecie().getNombre(),
                m.getNombre(),
                m.getRaza(),
                m.getSexo(),
                m.getFechaNacimiento(),
                m.getEdadAproximadaMeses(),
                EdadMascota.enDias(m, LocalDate.now()),
                m.getSenasParticulares(),
                m.getEstado(),
                m.getCreadoEn()
        );
    }
}
