package mx.edu.utez.veterinariaintegradora.dto.dueno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuenoRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres no deben exceder 80 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 60, message = "El apellido paterno no debe exceder 60 caracteres")
    private String apellidoPaterno;

    @Size(max = 60, message = "El apellido materno no debe exceder 60 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$", message = "El teléfono debe tener entre 7 y 20 dígitos o símbolos + ( ) -")
    private String telefono;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo no debe exceder 120 caracteres")
    private String correo;

    @Size(max = 200, message = "La dirección corta no debe exceder 200 caracteres")
    private String direccionCorta;

    /** Puede incluir preferencias de contacto. */
    @Size(max = 2000, message = "Las notas no deben exceder 2000 caracteres")
    private String notas;
}
