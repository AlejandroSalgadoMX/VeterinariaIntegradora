package mx.edu.utez.veterinariaintegradora.model.inventario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote_insumo", uniqueConstraints = {
        @UniqueConstraint(name = "uq_lote_insumo_codigo", columnNames = {"insumo_id", "codigo_lote"})
})
@Getter
@Setter
@NoArgsConstructor
public class LoteInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lote_insumo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 10)
    private UnidadMedida unidadMedida;

    @Column(name = "codigo_lote", nullable = false, length = 50)
    private String codigoLote;

    @Column(name = "cantidad_ingresada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadIngresada;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

}
