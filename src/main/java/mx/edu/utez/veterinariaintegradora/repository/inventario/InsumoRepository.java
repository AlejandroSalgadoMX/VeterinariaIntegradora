package mx.edu.utez.veterinariaintegradora.repository.inventario;

import mx.edu.utez.veterinariaintegradora.model.inventario.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
}
