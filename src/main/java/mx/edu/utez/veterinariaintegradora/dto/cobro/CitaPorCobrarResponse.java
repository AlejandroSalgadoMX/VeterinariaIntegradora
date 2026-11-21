package mx.edu.utez.veterinariaintegradora.dto.cobro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CitaPorCobrarResponse(
        Long citaId,
        Long cargoId,
        String mascotaNombre,
        String duenoNombre,
        String duenoTelefono,
        String veterinarioNombre,
        LocalDateTime fechaHoraInicio,
        BigDecimal total
) {
}
