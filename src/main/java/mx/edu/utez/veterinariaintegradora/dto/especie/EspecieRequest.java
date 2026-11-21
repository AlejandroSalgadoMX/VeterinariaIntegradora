package mx.edu.utez.veterinariaintegradora.dto.especie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EspecieRequest {

    @NotBlank(message = "El nombre de la especie es obligatorio")
    @Size(max = 50, message = "El nombre no debe exceder 50 caracteres")
    private String nombre;
}
