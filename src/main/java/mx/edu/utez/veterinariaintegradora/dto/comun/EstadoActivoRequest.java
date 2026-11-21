package mx.edu.utez.veterinariaintegradora.dto.comun;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoActivoRequest {

    @NotNull(message = "El campo activo es obligatorio")
    private Boolean activo;
}
