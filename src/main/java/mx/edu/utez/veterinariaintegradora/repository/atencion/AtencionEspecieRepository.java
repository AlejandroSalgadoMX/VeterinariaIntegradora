package mx.edu.utez.veterinariaintegradora.repository.atencion;

import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecie;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtencionEspecieRepository extends JpaRepository<AtencionEspecie, AtencionEspecieId> {

    List<AtencionEspecie> findByAtencion_Id(Long atencionId);

    boolean existsByAtencion_IdAndEspecie_Id(Long atencionId, Integer especieId);
}
