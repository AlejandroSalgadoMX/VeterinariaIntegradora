package mx.edu.utez.veterinariaintegradora.repository.atencion;

import mx.edu.utez.veterinariaintegradora.model.atencion.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Long> {
}
