package mx.edu.utez.veterinariaintegradora.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.auth.LoginRequest;
import mx.edu.utez.veterinariaintegradora.dto.auth.LoginResponse;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioResponse;
import mx.edu.utez.veterinariaintegradora.security.EndpointPublico;
import mx.edu.utez.veterinariaintegradora.security.UsuarioActual;
import mx.edu.utez.veterinariaintegradora.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @EndpointPublico
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse perfil(HttpServletRequest request) {
        return authService.perfil(UsuarioActual.de(request).id());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }
}
