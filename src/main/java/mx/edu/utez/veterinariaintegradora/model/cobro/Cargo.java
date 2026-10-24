package mx.edu.utez.veterinariaintegradora.model.cobro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;

import java.time.LocalDateTime;

@Entity
@Table(name = "cargo")
@Getter
@Setter
@NoArgsConstructor
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cargo_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @Column(name = "generado_en", nullable = false, updatable = false)
    private LocalDateTime generadoEn;

    @OneToOne(mappedBy = "cargo", fetch = FetchType.LAZY)
    private Pago pago;

    @PrePersist
    void asignarFechaGeneracion() {
        if (generadoEn == null) {
            generadoEn = LocalDateTime.now();
        }
    }
}
