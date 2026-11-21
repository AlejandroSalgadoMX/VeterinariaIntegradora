package mx.edu.utez.veterinariaintegradora.dto.veterinario;

import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;

public record VeterinarioResponse(
        Long id,
        Long usuarioId,
        String correo,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreCompleto,
        String cedulaProfesional,
        String especialidad,
        boolean activo
) {

    public static VeterinarioResponse from(Veterinario v) {
        return new VeterinarioResponse(
                v.getId(),
                v.getUsuario().getId(),
                v.getUsuario().getCorreo(),
                v.getNombres(),
                v.getApellidoPaterno(),
                v.getApellidoMaterno(),
                nombreCompleto(v),
                v.getCedulaProfesional(),
                v.getEspecialidad(),
                v.isActivo()
        );
    }

    public static String nombreCompleto(Veterinario v) {
        String completo = v.getNombres() + " " + v.getApellidoPaterno();
        return v.getApellidoMaterno() == null || v.getApellidoMaterno().isBlank()
                ? completo
                : completo + " " + v.getApellidoMaterno();
    }
}
