package mx.edu.utez.veterinariaintegradora.repository.dueno;

import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    @Query("select d from Dueno d where "
            + "lower(concat(d.nombres, ' ', d.apellidoPaterno, ' ', coalesce(d.apellidoMaterno, ''))) "
            + "like lower(concat('%', :texto, '%')) "
            + "or d.telefono like concat('%', :texto, '%') "
            + "or lower(coalesce(d.correo, '')) like lower(concat('%', :texto, '%')) "
            + "order by d.apellidoPaterno, d.nombres")
    List<Dueno> buscar(@Param("texto") String texto);
}
