package mx.edu.utez.veterinariaintegradora.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCrearRequest {

    @NotNull(message = "El rol es obligatorio (ADMINISTRADOR, RECEPCION o VETERINARIO)")
    private RolUsuario rol;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo no debe exceder 120 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    private Boolean activo;
}
