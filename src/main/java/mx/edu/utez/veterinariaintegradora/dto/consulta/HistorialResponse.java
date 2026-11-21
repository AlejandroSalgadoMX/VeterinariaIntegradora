package mx.edu.utez.veterinariaintegradora.dto.consulta;

import mx.edu.utez.veterinariaintegradora.model.cita.HistorialClinico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistorialResponse(
        Long id,
        Long citaId,
        Long mascotaId,
        BigDecimal pesoKg,
        String diagnostico,
        String indicaciones,
        String observaciones,
        LocalDateTime registradoEn
) {

    public static HistorialResponse from(HistorialClinico h) {
        return new HistorialResponse(h.getId(), h.getCita().getId(), h.getCita().getMascota().getId(),
                h.getPesoKg(), h.getDiagnostico(), h.getIndicaciones(), h.getObservaciones(), h.getRegistradoEn());
    }
}
