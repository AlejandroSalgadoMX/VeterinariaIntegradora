package mx.edu.utez.veterinariaintegradora.dto.cita;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCitaRequest {

    @NotNull(message = "El estado es obligatorio (EN_ATENCION, CANCELADA o NO_ASISTIO)")
    private EstadoCita estado;
}
