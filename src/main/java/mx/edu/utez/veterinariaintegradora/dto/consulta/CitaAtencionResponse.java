package mx.edu.utez.veterinariaintegradora.dto.consulta;

import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CitaAtencionResponse(
        Long id,
        Long citaId,
        Long atencionId,
        String atencionNombre,
        TipoAtencion tipo,
        BigDecimal precioAplicado,
        String observaciones,
        LocalDateTime aplicadaEn,
        List<UsoInsumoResponse> insumosUsados
) {

    public static CitaAtencionResponse from(CitaAtencion ca, List<UsoInsumoResponse> usos) {
        return new CitaAtencionResponse(
                ca.getId(),
                ca.getCita().getId(),
                ca.getAtencion().getId(),
                ca.getAtencion().getNombre(),
                ca.getAtencion().getTipo(),
                ca.getPrecioAplicado(),
                ca.getObservaciones(),
                ca.getAplicadaEn(),
                usos
        );
    }
}
