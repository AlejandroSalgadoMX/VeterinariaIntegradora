package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Long> {

    @Query("select coalesce(sum(u.cantidadUsada), 0) from UsoInsumo u where u.loteInsumo.id = :loteId")
    BigDecimal sumarCantidadUsadaPorLote(@Param("loteId") Long loteId);
}
