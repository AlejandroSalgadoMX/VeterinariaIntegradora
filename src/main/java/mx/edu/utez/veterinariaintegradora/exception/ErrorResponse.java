package mx.edu.utez.veterinariaintegradora.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String mensaje,
        Map<String, String> errores
) {

    public static ErrorResponse of(int estado, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), estado, mensaje, Map.of());
    }

    public static ErrorResponse of(int estado, String mensaje, Map<String, String> errores) {
        return new ErrorResponse(LocalDateTime.now(), estado, mensaje, errores);
    }
}
