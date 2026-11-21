package mx.edu.utez.veterinariaintegradora.dto.cobro;

import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CargoResponse(
        Long cargoId,
        Long citaId,
        EstadoCita estadoCita,
        Long mascotaId,
        String mascotaNombre,
        String duenoNombre,
        LocalDateTime generadoEn,
        List<ConceptoCargoResponse> conceptos,
        BigDecimal total,
        boolean pagado,
        PagoResponse pago
) {
}
