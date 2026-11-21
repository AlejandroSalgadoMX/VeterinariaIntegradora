package mx.edu.utez.veterinariaintegradora.dto.mascota;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoMascotaRequest {

    @NotNull(message = "El estado es obligatorio (ACTIVA, INACTIVA o FALLECIDA)")
    private EstadoMascota estado;
}
