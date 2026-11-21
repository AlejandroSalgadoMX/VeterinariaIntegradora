package mx.edu.utez.veterinariaintegradora.dto.veterinario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioRequest {

    @NotNull(message = "El usuario (con rol VETERINARIO) es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres no deben exceder 80 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 60, message = "El apellido paterno no debe exceder 60 caracteres")
    private String apellidoPaterno;

    @Size(max = 60, message = "El apellido materno no debe exceder 60 caracteres")
    private String apellidoMaterno;

    @Size(max = 30, message = "La cédula profesional no debe exceder 30 caracteres")
    private String cedulaProfesional;

    @Size(max = 100, message = "La especialidad no debe exceder 100 caracteres")
    private String especialidad;

    private Boolean activo;
}
