package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CitaAtencionRepository extends JpaRepository<CitaAtencion, Long> {

    List<CitaAtencion> findByCita_IdOrderByAplicadaEnAsc(Long citaId);

    boolean existsByCita_Id(Long citaId);

    /** Última vez que se aplicó la atención a la mascota (para validar el intervalo mínimo). */
    @Query("select max(ca.aplicadaEn) from CitaAtencion ca where ca.atencion.id = :atencionId "
            + "and ca.cita.mascota.id = :mascotaId and ca.cita.estado not in :estadosExcluidos")
    LocalDateTime buscarUltimaAplicacion(@Param("atencionId") Long atencionId,
                                         @Param("mascotaId") Long mascotaId,
                                         @Param("estadosExcluidos") Collection<EstadoCita> estadosExcluidos);
}
