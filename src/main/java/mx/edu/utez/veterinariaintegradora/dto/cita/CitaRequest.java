package mx.edu.utez.veterinariaintegradora.dto.cita;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitaRequest {

    @NotNull(message = "La mascota es obligatoria")
    private Long mascotaId;

    @NotNull(message = "El veterinario es obligatorio")
    private Long veterinarioId;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @FutureOrPresent(message = "No se pueden agendar citas en el pasado")
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "La fecha y hora de fin es obligatoria")
    private LocalDateTime fechaHoraFin;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 255, message = "El motivo no debe exceder 255 caracteres")
    private String motivo;
}
