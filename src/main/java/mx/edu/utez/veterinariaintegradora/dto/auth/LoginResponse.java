package mx.edu.utez.veterinariaintegradora.dto.auth;

import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioResponse;

import java.time.LocalDateTime;

public record LoginResponse(
        String token,
        String tipo,
        LocalDateTime expiraEn,
        UsuarioResponse usuario
) {
}
