package mx.edu.utez.veterinariaintegradora.dto.inventario;

import mx.edu.utez.veterinariaintegradora.model.inventario.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteResponse(
        Long id,
        Long insumoId,
        String insumoNombre,
        String codigoLote,
        UnidadMedida unidadMedida,
        BigDecimal cantidadIngresada,
        BigDecimal cantidadUtilizada,
        BigDecimal existenciaDisponible,
        LocalDate fechaIngreso,
        LocalDate fechaCaducidad,
        boolean caducado
) {
}
