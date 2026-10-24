package mx.edu.utez.veterinariaintegradora.model.atencion;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class AtencionInsumoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "atencion_id")
    private Long atencionId;

    @Column(name = "insumo_id")
    private Long insumoId;
}
