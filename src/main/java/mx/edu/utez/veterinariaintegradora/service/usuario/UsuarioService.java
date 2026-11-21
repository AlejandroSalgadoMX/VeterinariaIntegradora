package mx.edu.utez.veterinariaintegradora.service.usuario;

import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioActualizarRequest;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioCrearRequest;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;
import mx.edu.utez.veterinariaintegradora.repository.usuario.UsuarioRepository;
import mx.edu.utez.veterinariaintegradora.security.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(RolUsuario rol, Boolean activo) {
        return usuarioRepository.findAll().stream()
                .filter(u -> rol == null || u.getRol() == rol)
                .filter(u -> activo == null || u.isActivo() == activo)
                .sorted(Comparator.comparing(Usuario::getId))
                .map(UsuarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest request) {
        String correo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe un usuario con el correo " + correo + ".");
        }
        Usuario usuario = new Usuario();
        usuario.setRol(request.getRol());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordHasher.hash(request.getContrasena()));
        usuario.setActivo(request.getActivo() == null || request.getActivo());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest request) {
        Usuario usuario = buscar(id);
        String correo = normalizarCorreo(request.getCorreo());
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe otro usuario con el correo " + correo + ".");
        }
        if (usuario.getVeterinario() != null && request.getRol() != RolUsuario.VETERINARIO) {
            throw new ReglaNegocioException(
                    "El usuario tiene un perfil de veterinario vinculado; su rol debe seguir siendo VETERINARIO.");
        }
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR && request.getRol() != RolUsuario.ADMINISTRADOR) {
            validarQueNoEsUltimoAdministrador(usuario);
        }
        usuario.setRol(request.getRol());
        usuario.setCorreo(correo);
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasenaHash(passwordHasher.hash(request.getContrasena()));
        }
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscar(id);
        if (!activo && usuario.getRol() == RolUsuario.ADMINISTRADOR) {
            validarQueNoEsUltimoAdministrador(usuario);
        }
        usuario.setActivo(activo);
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }

    private void validarQueNoEsUltimoAdministrador(Usuario usuario) {
        if (usuario.isActivo() && usuarioRepository.countByRolAndActivoTrue(RolUsuario.ADMINISTRADOR) <= 1) {
            throw new ReglaNegocioException("Debe existir al menos un administrador activo en el sistema.");
        }
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }
}
