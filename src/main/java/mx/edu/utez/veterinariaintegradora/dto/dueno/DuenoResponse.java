package mx.edu.utez.veterinariaintegradora.dto.dueno;

import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;

import java.time.LocalDateTime;

public record DuenoResponse(
        Long id,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreCompleto,
        String telefono,
        String correo,
        String direccionCorta,
        String notas,
        LocalDateTime creadoEn,
        long totalMascotas
) {

    public static DuenoResponse from(Dueno d, long totalMascotas) {
        return new DuenoResponse(d.getId(), d.getNombres(), d.getApellidoPaterno(), d.getApellidoMaterno(),
                nombreCompleto(d), d.getTelefono(), d.getCorreo(), d.getDireccionCorta(), d.getNotas(),
                d.getCreadoEn(), totalMascotas);
    }

    public static String nombreCompleto(Dueno d) {
        String completo = d.getNombres() + " " + d.getApellidoPaterno();
        return d.getApellidoMaterno() == null || d.getApellidoMaterno().isBlank()
                ? completo
                : completo + " " + d.getApellidoMaterno();
    }
}
