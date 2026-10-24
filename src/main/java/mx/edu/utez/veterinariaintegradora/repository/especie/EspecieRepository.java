package mx.edu.utez.veterinariaintegradora.repository.especie;

import mx.edu.utez.veterinariaintegradora.model.especie.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Integer> {
}
