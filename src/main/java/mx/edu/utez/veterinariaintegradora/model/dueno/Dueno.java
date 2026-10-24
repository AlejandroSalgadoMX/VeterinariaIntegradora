package mx.edu.utez.veterinariaintegradora.model.dueno;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dueno")
@Getter
@Setter
@NoArgsConstructor
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dueno_id")
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 60)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 60)
    private String apellidoMaterno;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 120)
    private String correo;

    @Column(name = "direccion_corta", length = 200)
    private String direccionCorta;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "dueno", fetch = FetchType.LAZY)
    private Set<Mascota> mascotas = new HashSet<>();

    @PrePersist
    void asignarFechaCreacion() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
    }
}
