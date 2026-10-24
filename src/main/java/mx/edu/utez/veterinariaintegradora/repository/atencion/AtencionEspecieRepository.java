package mx.edu.utez.veterinariaintegradora.repository.atencion;

import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecie;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtencionEspecieRepository extends JpaRepository<AtencionEspecie, AtencionEspecieId> {
}
