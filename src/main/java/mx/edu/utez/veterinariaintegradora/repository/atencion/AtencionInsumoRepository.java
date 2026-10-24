package mx.edu.utez.veterinariaintegradora.repository.atencion;

import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumo;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtencionInsumoRepository extends JpaRepository<AtencionInsumo, AtencionInsumoId> {
}
