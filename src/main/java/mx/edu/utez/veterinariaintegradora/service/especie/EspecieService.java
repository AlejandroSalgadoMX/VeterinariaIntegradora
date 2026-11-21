package mx.edu.utez.veterinariaintegradora.service.especie;

import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieRequest;
import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.especie.Especie;
import mx.edu.utez.veterinariaintegradora.repository.especie.EspecieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspecieService {

    private final EspecieRepository especieRepository;

    public EspecieService(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
    }

    @Transactional(readOnly = true)
    public List<EspecieResponse> listar() {
        return especieRepository.findAll(Sort.by("nombre")).stream().map(EspecieResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EspecieResponse obtener(Integer id) {
        return EspecieResponse.from(buscar(id));
    }

    @Transactional
    public EspecieResponse crear(EspecieRequest request) {
        String nombre = request.getNombre().trim();
        if (especieRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("La especie " + nombre + " ya está registrada.");
        }
        Especie especie = new Especie();
        especie.setNombre(nombre);
        return EspecieResponse.from(especieRepository.save(especie));
    }

    @Transactional
    public EspecieResponse actualizar(Integer id, EspecieRequest request) {
        Especie especie = buscar(id);
        String nombre = request.getNombre().trim();
        if (especieRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("La especie " + nombre + " ya está registrada.");
        }
        especie.setNombre(nombre);
        return EspecieResponse.from(especieRepository.save(especie));
    }

    @Transactional(readOnly = true)
    public Especie buscar(Integer id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie", id));
    }
}
