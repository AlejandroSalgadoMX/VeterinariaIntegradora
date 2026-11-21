package mx.edu.utez.veterinariaintegradora.dto.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtencionRequest {

    @NotNull(message = "El tipo es obligatorio (CONSULTA, VACUNA, ESTUDIO o PROCEDIMIENTO)")
    private TipoAtencion tipo;

    @NotBlank(message = "El nombre de la atención es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no debe exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio base no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 enteros y 2 decimales")
    private BigDecimal precioBase;

    @NotNull(message = "La duración estimada es obligatoria")
    @Min(value = 1, message = "La duración debe ser de al menos 1 minuto")
    @Max(value = 1440, message = "La duración no debe exceder 1440 minutos")
    private Integer duracionMinutos;

    /** Opcional: si es nulo no se valida la edad mínima. */
    @Min(value = 0, message = "La edad mínima no puede ser negativa")
    @Max(value = 65535, message = "La edad mínima no debe exceder 65535 días")
    private Integer edadMinimaDias;

    /** Opcional: si es nulo no se valida el intervalo entre aplicaciones. */
    @Min(value = 1, message = "El intervalo mínimo debe ser de al menos 1 día")
    @Max(value = 65535, message = "El intervalo no debe exceder 65535 días")
    private Integer intervaloMinimoDias;

    @NotEmpty(message = "Indica al menos una especie a la que aplica la atención")
    private List<@NotNull(message = "El id de especie no puede ser nulo") Integer> especieIds = new ArrayList<>();

    @Valid
    private List<InsumoRequeridoRequest> insumosRequeridos = new ArrayList<>();

    private Boolean activa;
}
