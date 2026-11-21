package mx.edu.utez.veterinariaintegradora.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.veterinariaintegradora.exception.AccesoDenegadoException;
import mx.edu.utez.veterinariaintegradora.exception.NoAutenticadoException;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;
import mx.edu.utez.veterinariaintegradora.repository.usuario.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Regla del DFR: "Solo usuarios activos, con rol asignado, operan según sus permisos".
 * Valida el token Bearer, que el usuario siga activo y que su rol esté permitido.
 */
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    private static final String PREFIJO = "Bearer ";

    private final TokenStore tokenStore;
    private final UsuarioRepository usuarioRepository;

    public AutenticacionInterceptor(TokenStore tokenStore, UsuarioRepository usuarioRepository) {
        this.tokenStore = tokenStore;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || !(handler instanceof HandlerMethod metodo)) {
            return true;
        }
        if (metodo.hasMethodAnnotation(EndpointPublico.class)
                || metodo.getBeanType().isAnnotationPresent(EndpointPublico.class)) {
            return true;
        }

        String encabezado = request.getHeader("Authorization");
        if (encabezado == null || !encabezado.startsWith(PREFIJO)) {
            throw new NoAutenticadoException("Debes iniciar sesión y enviar el encabezado Authorization: Bearer <token>.");
        }
        String token = encabezado.substring(PREFIJO.length()).trim();
        Long usuarioId = tokenStore.obtenerUsuarioId(token)
                .orElseThrow(() -> new NoAutenticadoException("El token no es válido o ya expiró."));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoAutenticadoException("El usuario de la sesión ya no existe."));
        if (!usuario.isActivo()) {
            tokenStore.revocar(token);
            throw new NoAutenticadoException("El usuario está inactivo.");
        }
        if (usuario.getRol() == null) {
            throw new AccesoDenegadoException("El usuario no tiene un rol asignado.");
        }

        RolesPermitidos roles = metodo.getMethodAnnotation(RolesPermitidos.class);
        if (roles == null) {
            roles = metodo.getBeanType().getAnnotation(RolesPermitidos.class);
        }
        if (roles != null && usuario.getRol() != RolUsuario.ADMINISTRADOR
                && Arrays.stream(roles.value()).noneMatch(rol -> rol == usuario.getRol())) {
            throw new AccesoDenegadoException("Tu rol (" + usuario.getRol() + ") no tiene permiso para esta operación.");
        }

        request.setAttribute(UsuarioActual.ATRIBUTO,
                new UsuarioActual(usuario.getId(), usuario.getCorreo(), usuario.getRol()));
        return true;
    }
}
