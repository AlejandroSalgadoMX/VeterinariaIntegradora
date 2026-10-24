package mx.edu.utez.veterinariaintegradora.model.atencion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.especie.Especie;

@Entity
@Table(name = "atencion_especie")
@Getter
@Setter
@NoArgsConstructor
public class AtencionEspecie {

    @EmbeddedId
    private AtencionEspecieId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("atencionId")
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("especieId")
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;
}
