package mx.edu.utez.veterinariaintegradora.model.atencion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.inventario.Insumo;

import java.math.BigDecimal;

@Entity
@Table(name = "atencion_insumo")
@Getter
@Setter
@NoArgsConstructor
public class AtencionInsumo {

    @EmbeddedId
    private AtencionInsumoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("atencionId")
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("insumoId")
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(name = "cantidad_requerida", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadRequerida;
}
