package mx.edu.utez.veterinariaintegradora.security;

import jakarta.servlet.http.HttpServletRequest;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;

/** Datos del usuario autenticado que el interceptor deja en la petición. */
public record UsuarioActual(Long id, String correo, RolUsuario rol) {

    public static final String ATRIBUTO = "usuarioActual";

    public static UsuarioActual de(HttpServletRequest request) {
        return (UsuarioActual) request.getAttribute(ATRIBUTO);
    }
}
