package mx.edu.utez.veterinariaintegradora.dto.consulta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialRequest {

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor que cero")
    @Digits(integer = 3, fraction = 2, message = "El peso debe tener máximo 3 enteros y 2 decimales")
    private BigDecimal pesoKg;

    @Size(max = 5000, message = "El diagnóstico no debe exceder 5000 caracteres")
    private String diagnostico;

    @Size(max = 5000, message = "Las indicaciones no deben exceder 5000 caracteres")
    private String indicaciones;

    @Size(max = 5000, message = "Las observaciones no deben exceder 5000 caracteres")
    private String observaciones;
}
