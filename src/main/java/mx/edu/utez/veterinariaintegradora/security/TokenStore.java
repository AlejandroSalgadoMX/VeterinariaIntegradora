package mx.edu.utez.veterinariaintegradora.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Sesiones en memoria: token opaco -> usuario. Se pierden al reiniciar la aplicación. */
@Component
public class TokenStore {

    private record Sesion(Long usuarioId, LocalDateTime expiraEn) {
    }

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final long horasVigencia;

    public TokenStore(@Value("${app.auth.horas-vigencia-token:8}") long horasVigencia) {
        this.horasVigencia = horasVigencia;
    }

    public String crear(Long usuarioId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new Sesion(usuarioId, LocalDateTime.now().plusHours(horasVigencia)));
        return token;
    }

    public Optional<Long> obtenerUsuarioId(String token) {
        Sesion sesion = sesiones.get(token);
        if (sesion == null) {
            return Optional.empty();
        }
        if (sesion.expiraEn().isBefore(LocalDateTime.now())) {
            sesiones.remove(token);
            return Optional.empty();
        }
        return Optional.of(sesion.usuarioId());
    }

    public LocalDateTime expiraEn(String token) {
        Sesion sesion = sesiones.get(token);
        return sesion != null ? sesion.expiraEn() : null;
    }

    public void revocar(String token) {
        sesiones.remove(token);
    }
}
