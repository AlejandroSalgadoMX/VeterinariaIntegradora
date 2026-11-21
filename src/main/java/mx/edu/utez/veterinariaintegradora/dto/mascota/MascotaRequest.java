package mx.edu.utez.veterinariaintegradora.dto.mascota;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;
import mx.edu.utez.veterinariaintegradora.model.mascota.Sexo;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MascotaRequest {

    @NotNull(message = "El dueño es obligatorio")
    private Long duenoId;

    @NotNull(message = "La especie es obligatoria")
    private Integer especieId;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 80, message = "El nombre no debe exceder 80 caracteres")
    private String nombre;

    @Size(max = 80, message = "La raza no debe exceder 80 caracteres")
    private String raza;

    @NotNull(message = "El sexo es obligatorio (MACHO o HEMBRA)")
    private Sexo sexo;

    /** Enviar fecha de nacimiento o edad aproximada en meses (solo uno de los dos). */
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;

    @Min(value = 0, message = "La edad aproximada no puede ser negativa")
    @Max(value = 65535, message = "La edad aproximada no debe exceder 65535 meses")
    private Integer edadAproximadaMeses;

    @Size(max = 255, message = "Las señas particulares no deben exceder 255 caracteres")
    private String senasParticulares;

    private EstadoMascota estado;
}
