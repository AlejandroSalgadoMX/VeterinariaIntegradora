package mx.edu.utez.veterinariaintegradora.dto.usuario;

import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        RolUsuario rol,
        String correo,
        boolean activo,
        LocalDateTime creadoEn,
        Long veterinarioId
) {

    public static UsuarioResponse from(Usuario usuario) {
        Long veterinarioId = usuario.getVeterinario() != null ? usuario.getVeterinario().getId() : null;
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRol(),
                usuario.getCorreo(),
                usuario.isActivo(),
                usuario.getCreadoEn(),
                veterinarioId
        );
    }
}
