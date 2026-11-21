package mx.edu.utez.veterinariaintegradora.dto.cobro;

import java.math.BigDecimal;

public record ConceptoCargoResponse(
        String tipo,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
