package mx.edu.utez.veterinariaintegradora.repository.inventario;

import jakarta.persistence.LockModeType;
import mx.edu.utez.veterinariaintegradora.model.inventario.LoteInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteInsumoRepository extends JpaRepository<LoteInsumo, Long> {

    List<LoteInsumo> findByInsumo_IdOrderByFechaCaducidadAscIdAsc(Long insumoId);

    boolean existsByInsumo_IdAndCodigoLoteIgnoreCase(Long insumoId, String codigoLote);

    List<LoteInsumo> findByFechaCaducidadBetweenOrderByFechaCaducidadAsc(LocalDate desde, LocalDate hasta);

    /** Bloquea el lote para descontar existencias sin permitir negativos por concurrencia. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LoteInsumo l where l.id = :id")
    Optional<LoteInsumo> findByIdParaUso(@Param("id") Long id);

    /** Lotes vigentes del insumo, primero los que caducan antes (FEFO), bloqueados para uso. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LoteInsumo l where l.insumo.id = :insumoId and l.fechaCaducidad >= :hoy "
            + "order by l.fechaCaducidad asc, l.id asc")
    List<LoteInsumo> findVigentesParaUso(@Param("insumoId") Long insumoId, @Param("hoy") LocalDate hoy);
}
