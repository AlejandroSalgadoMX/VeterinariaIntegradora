package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
}
