package mx.edu.utez.veterinariaintegradora.dto.consulta;

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
public class UsoInsumoRequest {

    @NotNull(message = "El lote del insumo es obligatorio")
    private Long loteInsumoId;

    @NotNull(message = "La cantidad usada es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad usada debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "La cantidad debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal cantidadUsada;

    /** Si es cobrable (por defecto sí) se cobra al precio de venta del insumo. */
    private Boolean cobrable;
}
