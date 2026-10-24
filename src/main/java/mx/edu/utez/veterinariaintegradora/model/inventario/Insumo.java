package mx.edu.utez.veterinariaintegradora.model.inventario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "insumo")
@Getter
@Setter
@NoArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "precio_venta_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVentaBase = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "insumo", fetch = FetchType.LAZY)
    private Set<AtencionInsumo> atencionesRequeridas = new HashSet<>();

    @OneToMany(mappedBy = "insumo", fetch = FetchType.LAZY)
    private Set<LoteInsumo> lotes = new HashSet<>();
}
