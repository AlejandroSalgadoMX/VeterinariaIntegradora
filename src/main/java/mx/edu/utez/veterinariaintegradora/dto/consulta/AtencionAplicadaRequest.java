package mx.edu.utez.veterinariaintegradora.dto.consulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtencionAplicadaRequest {

    @NotNull(message = "La atención del catálogo es obligatoria")
    private Long atencionId;

    @Size(max = 255, message = "Las observaciones no deben exceder 255 caracteres")
    private String observaciones;

    /** Opcional: lotes elegidos para los insumos obligatorios de la atención. */
    @Valid
    private List<LoteSeleccionRequest> lotes = new ArrayList<>();
}
