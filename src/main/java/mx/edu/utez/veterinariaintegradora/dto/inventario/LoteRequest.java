package mx.edu.utez.veterinariaintegradora.dto.inventario;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.inventario.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteRequest {

    @NotBlank(message = "El código de lote es obligatorio")
    @Size(max = 50, message = "El código de lote no debe exceder 50 caracteres")
    private String codigoLote;

    @NotNull(message = "La unidad de medida es obligatoria (PIEZA, ML, MG, G, KG, DOSIS, TABLETA o FRASCO)")
    private UnidadMedida unidadMedida;

    @NotNull(message = "La cantidad ingresada es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad ingresada debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "La cantidad debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal cantidadIngresada;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    @NotNull(message = "La fecha de caducidad es obligatoria")
    private LocalDate fechaCaducidad;
}
