package mx.edu.utez.veterinariaintegradora.repository.cita;

import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(LocalDateTime desde, LocalDateTime hasta);

    List<Cita> findByMascota_IdOrderByFechaHoraInicioDesc(Long mascotaId);

    List<Cita> findByEstadoOrderByFechaHoraInicioAsc(EstadoCita estado);

    /** Hay traslape si otra cita activa del veterinario empieza antes del fin y termina después del inicio. */
    @Query("select case when count(c) > 0 then true else false end from Cita c where c.veterinario.id = :veterinarioId "
            + "and c.id <> :citaExcluida and c.estado not in :estadosLibres "
            + "and c.fechaHoraInicio < :fin and c.fechaHoraFin > :inicio")
    boolean existeTraslapeVeterinario(@Param("veterinarioId") Long veterinarioId,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fin") LocalDateTime fin,
                                      @Param("citaExcluida") Long citaExcluida,
                                      @Param("estadosLibres") Collection<EstadoCita> estadosLibres);

    @Query("select case when count(c) > 0 then true else false end from Cita c where c.mascota.id = :mascotaId "
            + "and c.id <> :citaExcluida and c.estado not in :estadosLibres "
            + "and c.fechaHoraInicio < :fin and c.fechaHoraFin > :inicio")
    boolean existeTraslapeMascota(@Param("mascotaId") Long mascotaId,
                                  @Param("inicio") LocalDateTime inicio,
                                  @Param("fin") LocalDateTime fin,
                                  @Param("citaExcluida") Long citaExcluida,
                                  @Param("estadosLibres") Collection<EstadoCita> estadosLibres);
}
