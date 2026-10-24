package mx.edu.utez.veterinariaintegradora.repository.dueno;

import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {
}
