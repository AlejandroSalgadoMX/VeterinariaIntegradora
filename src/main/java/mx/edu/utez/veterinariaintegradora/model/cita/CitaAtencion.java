package mx.edu.utez.veterinariaintegradora.model.cita;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.atencion.Atencion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cita_atencion", indexes = {
        @Index(name = "ix_cita_atencion_historial", columnList = "atencion_id,aplicada_en")
})
@Getter
@Setter
@NoArgsConstructor
public class CitaAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cita_atencion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAplicado;

    @Column(length = 255)
    private String observaciones;

    @Column(name = "aplicada_en", nullable = false, updatable = false)
    private LocalDateTime aplicadaEn;

    @OneToMany(mappedBy = "citaAtencion", fetch = FetchType.LAZY)
    private Set<UsoInsumo> usosInsumo = new HashSet<>();

    @PrePersist
    void asignarFechaAplicacion() {
        if (aplicadaEn == null) {
            aplicadaEn = LocalDateTime.now();
        }
    }
}
