package mx.edu.utez.veterinariaintegradora.model.atencion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "atencion")
@Getter
@Setter
@NoArgsConstructor
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atencion_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAtencion tipo;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "edad_minima_dias")
    private Integer edadMinimaDias;

    @Column(name = "intervalo_minimo_dias")
    private Integer intervaloMinimoDias;

    @Column(nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "atencion", fetch = FetchType.LAZY)
    private Set<AtencionEspecie> especiesCompatibles = new HashSet<>();

    @OneToMany(mappedBy = "atencion", fetch = FetchType.LAZY)
    private Set<AtencionInsumo> insumosRequeridos = new HashSet<>();

}
