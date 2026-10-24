package mx.edu.utez.veterinariaintegradora.model.especie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "especie")
@Getter
@Setter
@NoArgsConstructor
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "especie_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @OneToMany(mappedBy = "especie", fetch = FetchType.LAZY)
    private Set<Mascota> mascotas = new HashSet<>();

}
