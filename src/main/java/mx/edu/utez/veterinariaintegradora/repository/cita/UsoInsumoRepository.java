package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Long> {
}
