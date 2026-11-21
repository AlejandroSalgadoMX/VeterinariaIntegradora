package mx.edu.utez.veterinariaintegradora.service.mascota;

import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaRequest;
import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.dueno.Dueno;
import mx.edu.utez.veterinariaintegradora.model.especie.Especie;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;
import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;
import mx.edu.utez.veterinariaintegradora.repository.dueno.DuenoRepository;
import mx.edu.utez.veterinariaintegradora.repository.especie.EspecieRepository;
import mx.edu.utez.veterinariaintegradora.repository.mascota.MascotaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** HU-REC-02 · Mascotas: toda mascota tiene un dueño; un dueño puede tener varias. */
@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;
    private final EspecieRepository especieRepository;

    public MascotaService(MascotaRepository mascotaRepository,
                          DuenoRepository duenoRepository,
                          EspecieRepository especieRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
        this.especieRepository = especieRepository;
    }

    @Transactional(readOnly = true)
    public List<MascotaResponse> listar(Long duenoId, Integer especieId, EstadoMascota estado, String nombre) {
        List<Mascota> mascotas = duenoId != null
                ? mascotaRepository.findByDueno_IdOrderByNombreAsc(duenoId)
                : mascotaRepository.findAll(Sort.by("nombre"));
        return mascotas.stream()
                .filter(m -> especieId == null || m.getEspecie().getId().equals(especieId))
                .filter(m -> estado == null || m.getEstado() == estado)
                .filter(m -> nombre == null || m.getNombre().toLowerCase().contains(nombre.trim().toLowerCase()))
                .map(MascotaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MascotaResponse> listarPorDueno(Long duenoId) {
        if (!duenoRepository.existsById(duenoId)) {
            throw new RecursoNoEncontradoException("Dueño", duenoId);
        }
        return mascotaRepository.findByDueno_IdOrderByNombreAsc(duenoId).stream().map(MascotaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MascotaResponse obtener(Long id) {
        return MascotaResponse.from(buscar(id));
    }

    @Transactional
    public MascotaResponse crear(MascotaRequest request) {
        Mascota mascota = new Mascota();
        copiarDatos(request, mascota);
        mascota.setEstado(request.getEstado() == null ? EstadoMascota.ACTIVA : request.getEstado());
        return MascotaResponse.from(mascotaRepository.save(mascota));
    }

    @Transactional
    public MascotaResponse actualizar(Long id, MascotaRequest request) {
        Mascota mascota = buscar(id);
        copiarDatos(request, mascota);
        if (request.getEstado() != null) {
            mascota.setEstado(request.getEstado());
        }
        return MascotaResponse.from(mascotaRepository.save(mascota));
    }

    @Transactional
    public MascotaResponse cambiarEstado(Long id, EstadoMascota estado) {
        Mascota mascota = buscar(id);
        mascota.setEstado(estado);
        return MascotaResponse.from(mascotaRepository.save(mascota));
    }

    @Transactional(readOnly = true)
    public Mascota buscar(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota", id));
    }

    private void copiarDatos(MascotaRequest request, Mascota mascota) {
        boolean tieneFecha = request.getFechaNacimiento() != null;
        boolean tieneEdad = request.getEdadAproximadaMeses() != null;
        if (tieneFecha == tieneEdad) {
            throw new ReglaNegocioException(
                    "Indica la fecha de nacimiento o la edad aproximada en meses (solo uno de los dos).");
        }
        Dueno dueno = duenoRepository.findById(request.getDuenoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Dueño", request.getDuenoId()));
        Especie especie = especieRepository.findById(request.getEspecieId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie", request.getEspecieId()));

        mascota.setDueno(dueno);
        mascota.setEspecie(especie);
        mascota.setNombre(request.getNombre().trim());
        mascota.setRaza(request.getRaza() == null || request.getRaza().isBlank() ? null : request.getRaza().trim());
        mascota.setSexo(request.getSexo());
        mascota.setFechaNacimiento(request.getFechaNacimiento());
        mascota.setEdadAproximadaMeses(request.getEdadAproximadaMeses());
        mascota.setSenasParticulares(request.getSenasParticulares() == null || request.getSenasParticulares().isBlank()
                ? null : request.getSenasParticulares().trim());
    }
}
