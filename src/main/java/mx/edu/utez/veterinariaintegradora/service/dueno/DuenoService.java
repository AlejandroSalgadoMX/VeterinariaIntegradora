package mx.edu.utez.veterinariaintegradora.service.dueno;

import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoRequest;
import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;
import mx.edu.utez.veterinariaintegradora.repository.dueno.DuenoRepository;
import mx.edu.utez.veterinariaintegradora.repository.mascota.MascotaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** HU-REC-01 · Dueños */
@Service
public class DuenoService {

    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;

    public DuenoService(DuenoRepository duenoRepository, MascotaRepository mascotaRepository) {
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
    }

    @Transactional(readOnly = true)
    public List<DuenoResponse> listar(String busqueda) {
        List<Dueno> duenos = busqueda == null || busqueda.isBlank()
                ? duenoRepository.findAll(Sort.by("apellidoPaterno", "nombres"))
                : duenoRepository.buscar(busqueda.trim());
        return duenos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DuenoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public DuenoResponse crear(DuenoRequest request) {
        Dueno dueno = new Dueno();
        copiarDatos(request, dueno);
        return toResponse(duenoRepository.save(dueno));
    }

    @Transactional
    public DuenoResponse actualizar(Long id, DuenoRequest request) {
        Dueno dueno = buscar(id);
        copiarDatos(request, dueno);
        return toResponse(duenoRepository.save(dueno));
    }

    /** Solo se elimina un dueño registrado por error que aún no tiene mascotas. */
    @Transactional
    public void eliminar(Long id) {
        Dueno dueno = buscar(id);
        if (mascotaRepository.countByDueno_Id(id) > 0) {
            throw new ReglaNegocioException("No se puede eliminar un dueño con mascotas registradas.");
        }
        duenoRepository.delete(dueno);
    }

    @Transactional(readOnly = true)
    public Dueno buscar(Long id) {
        return duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueño", id));
    }

    private void copiarDatos(DuenoRequest request, Dueno dueno) {
        dueno.setNombres(request.getNombres().trim());
        dueno.setApellidoPaterno(request.getApellidoPaterno().trim());
        dueno.setApellidoMaterno(limpiar(request.getApellidoMaterno()));
        dueno.setTelefono(request.getTelefono().trim());
        dueno.setCorreo(request.getCorreo() == null || request.getCorreo().isBlank()
                ? null : request.getCorreo().trim().toLowerCase());
        dueno.setDireccionCorta(limpiar(request.getDireccionCorta()));
        dueno.setNotas(limpiar(request.getNotas()));
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private DuenoResponse toResponse(Dueno dueno) {
        return DuenoResponse.from(dueno, mascotaRepository.countByDueno_Id(dueno.getId()));
    }
}
