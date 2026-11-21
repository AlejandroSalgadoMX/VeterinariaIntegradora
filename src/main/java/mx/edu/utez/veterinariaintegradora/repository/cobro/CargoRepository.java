package mx.edu.utez.veterinariaintegradora.repository.cobro;

import mx.edu.utez.veterinariaintegradora.model.cobro.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

    Optional<Cargo> findByCita_Id(Long citaId);

    boolean existsByCita_Id(Long citaId);
}
