package mx.edu.utez.veterinariaintegradora.model.cita;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.inventario.LoteInsumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "uso_insumo", indexes = {
        @Index(name = "ix_uso_lote", columnList = "lote_insumo_id")
})
@Getter
@Setter
@NoArgsConstructor
public class UsoInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uso_insumo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_atencion_id", nullable = false)
    private CitaAtencion citaAtencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_insumo_id", nullable = false)
    private LoteInsumo loteInsumo;

    @Column(name = "cantidad_usada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadUsada;

    @Column(name = "precio_cobrado_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCobradoUnitario = BigDecimal.ZERO;

    @Column(name = "usado_en", nullable = false, updatable = false)
    private LocalDateTime usadoEn;

    @PrePersist
    void asignarFechaUso() {
        if (usadoEn == null) {
            usadoEn = LocalDateTime.now();
        }
    }
}
