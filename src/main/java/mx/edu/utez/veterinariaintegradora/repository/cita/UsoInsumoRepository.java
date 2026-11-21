package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Long> {

    /** Devuelve null si el lote aún no tiene usos. */
    @Query("select sum(u.cantidadUsada) from UsoInsumo u where u.loteInsumo.id = :loteId")
    BigDecimal sumarCantidadUsadaPorLote(@Param("loteId") Long loteId);

    List<UsoInsumo> findByCitaAtencion_IdOrderByUsadoEnAsc(Long citaAtencionId);

    List<UsoInsumo> findByCitaAtencion_Cita_IdOrderByUsadoEnAsc(Long citaId);
}
