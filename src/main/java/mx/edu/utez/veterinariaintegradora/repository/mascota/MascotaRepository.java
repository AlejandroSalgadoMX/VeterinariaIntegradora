package mx.edu.utez.veterinariaintegradora.repository.mascota;

import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}
