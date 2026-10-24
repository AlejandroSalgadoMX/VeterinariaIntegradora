package mx.edu.utez.veterinariaintegradora.model.cita;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_clinico")
@Getter
@Setter
@NoArgsConstructor
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_clinico_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @Column(name = "peso_kg", precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "registrado_en", nullable = false, updatable = false)
    private LocalDateTime registradoEn;

    @PrePersist
    void asignarFechaRegistro() {
        if (registradoEn == null) {
            registradoEn = LocalDateTime.now();
        }
    }
}
