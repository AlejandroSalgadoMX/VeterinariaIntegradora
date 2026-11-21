package mx.edu.utez.veterinariaintegradora.repository.mascota;

import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByDueno_IdOrderByNombreAsc(Long duenoId);

    long countByDueno_Id(Long duenoId);
}
