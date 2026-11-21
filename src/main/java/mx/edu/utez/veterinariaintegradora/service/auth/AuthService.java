package mx.edu.utez.veterinariaintegradora.service.auth;

import mx.edu.utez.veterinariaintegradora.dto.auth.LoginRequest;
import mx.edu.utez.veterinariaintegradora.dto.auth.LoginResponse;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioResponse;
import mx.edu.utez.veterinariaintegradora.exception.NoAutenticadoException;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;
import mx.edu.utez.veterinariaintegradora.repository.usuario.UsuarioRepository;
import mx.edu.utez.veterinariaintegradora.security.PasswordHasher;
import mx.edu.utez.veterinariaintegradora.security.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final TokenStore tokenStore;

    public AuthService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher, TokenStore tokenStore) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.tokenStore = tokenStore;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo().trim())
                .filter(u -> passwordHasher.coincide(request.getContrasena(), u.getContrasenaHash()))
                .orElseThrow(() -> new NoAutenticadoException("Correo o contraseña incorrectos."));
        if (!usuario.isActivo()) {
            throw new NoAutenticadoException("El usuario está inactivo; contacta al administrador.");
        }
        String token = tokenStore.crear(usuario.getId());
        return new LoginResponse(token, "Bearer", tokenStore.expiraEn(token), UsuarioResponse.from(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse perfil(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new NoAutenticadoException("El usuario de la sesión ya no existe."));
    }

    public void logout(String encabezadoAuthorization) {
        if (encabezadoAuthorization != null && encabezadoAuthorization.startsWith("Bearer ")) {
            tokenStore.revocar(encabezadoAuthorization.substring("Bearer ".length()).trim());
        }
    }
}
