package mx.edu.utez.veterinariaintegradora.dto.atencion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsumoRequeridoRequest {

    @NotNull(message = "El insumo requerido es obligatorio")
    private Long insumoId;

    @NotNull(message = "La cantidad requerida es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad requerida debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "La cantidad debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal cantidadRequerida;
}
