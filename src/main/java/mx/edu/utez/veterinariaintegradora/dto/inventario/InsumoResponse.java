package mx.edu.utez.veterinariaintegradora.dto.inventario;

import java.math.BigDecimal;

public record InsumoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precioVentaBase,
        boolean activo,
        BigDecimal existenciaVigente,
        int lotesVigentes
) {
}
