package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaAtencionRepository extends JpaRepository<CitaAtencion, Long> {
}
