package mx.edu.utez.veterinariaintegradora.repository.cobro;

import mx.edu.utez.veterinariaintegradora.model.cobro.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
}
