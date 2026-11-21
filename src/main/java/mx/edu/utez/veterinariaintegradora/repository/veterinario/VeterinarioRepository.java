package mx.edu.utez.veterinariaintegradora.repository.veterinario;

import jakarta.persistence.LockModeType;
import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_IdAndIdNot(Long usuarioId, Long id);

    boolean existsByCedulaProfesionalIgnoreCase(String cedulaProfesional);

    boolean existsByCedulaProfesionalIgnoreCaseAndIdNot(String cedulaProfesional, Long id);

    /** Bloquea al veterinario mientras se valida su agenda para evitar traslapes concurrentes. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Veterinario v where v.id = :id")
    Optional<Veterinario> findByIdParaAgendar(@Param("id") Long id);
}
