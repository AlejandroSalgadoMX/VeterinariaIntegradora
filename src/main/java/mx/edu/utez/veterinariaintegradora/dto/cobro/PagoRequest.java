package mx.edu.utez.veterinariaintegradora.dto.cobro;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.cobro.FormaPago;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {

    @NotNull(message = "La forma de pago es obligatoria (EFECTIVO, TARJETA o TRANSFERENCIA)")
    private FormaPago formaPago;

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El monto debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal montoPagado;
}
