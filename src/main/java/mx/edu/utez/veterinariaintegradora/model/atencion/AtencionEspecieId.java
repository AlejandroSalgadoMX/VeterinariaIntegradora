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
public class AtencionEspecieId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "atencion_id")
    private Long atencionId;

    @Column(name = "especie_id")
    private Integer especieId;
}
