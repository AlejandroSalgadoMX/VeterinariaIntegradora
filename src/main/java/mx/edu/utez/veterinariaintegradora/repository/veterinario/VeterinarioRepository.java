package mx.edu.utez.veterinariaintegradora.repository.veterinario;

import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
}
