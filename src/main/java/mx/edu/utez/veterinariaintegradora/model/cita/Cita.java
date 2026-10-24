package mx.edu.utez.veterinariaintegradora.model.cita;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cita", indexes = {
        @Index(name = "ix_cita_veterinario_horario", columnList = "veterinario_id,fecha_hora_inicio,fecha_hora_fin"),
        @Index(name = "ix_cita_mascota", columnList = "mascota_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cita_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @OneToOne(mappedBy = "cita", fetch = FetchType.LAZY)
    private HistorialClinico historialClinico;

    @OneToMany(mappedBy = "cita", fetch = FetchType.LAZY)
    private Set<CitaAtencion> atencionesAplicadas = new HashSet<>();

    @PrePersist
    void asignarFechaCreacion() {
        if (creadaEn == null) {
            creadaEn = LocalDateTime.now();
        }
    }
}
