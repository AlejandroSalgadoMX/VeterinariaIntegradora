package mx.edu.utez.veterinariaintegradora.dto.consulta;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Permite elegir el lote para un insumo obligatorio; si no se envía, se usa el lote vigente que caduca primero. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteSeleccionRequest {

    @NotNull(message = "El insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "El lote es obligatorio")
    private Long loteInsumoId;
}
