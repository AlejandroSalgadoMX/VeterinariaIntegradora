package mx.edu.utez.veterinariaintegradora.service.atencion;

import mx.edu.utez.veterinariaintegradora.dto.atencion.AtencionRequest;
import mx.edu.utez.veterinariaintegradora.dto.atencion.AtencionResponse;
import mx.edu.utez.veterinariaintegradora.dto.atencion.InsumoRequeridoRequest;
import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.atencion.Atencion;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecie;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionEspecieId;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumo;
import mx.edu.utez.veterinariaintegradora.model.atencion.AtencionInsumoId;
import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;
import mx.edu.utez.veterinariaintegradora.model.especie.Especie;
import mx.edu.utez.veterinariaintegradora.model.inventario.Insumo;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionEspecieRepository;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionInsumoRepository;
import mx.edu.utez.veterinariaintegradora.repository.atencion.AtencionRepository;
import mx.edu.utez.veterinariaintegradora.repository.especie.EspecieRepository;
import mx.edu.utez.veterinariaintegradora.repository.inventario.InsumoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** HU-ADM-03 · Catálogo de atenciones (especies compatibles, edad mínima, intervalo e insumos obligatorios). */
@Service
public class AtencionService {

    private final AtencionRepository atencionRepository;
    private final AtencionEspecieRepository atencionEspecieRepository;
    private final AtencionInsumoRepository atencionInsumoRepository;
    private final EspecieRepository especieRepository;
    private final InsumoRepository insumoRepository;

    public AtencionService(AtencionRepository atencionRepository,
                           AtencionEspecieRepository atencionEspecieRepository,
                           AtencionInsumoRepository atencionInsumoRepository,
                           EspecieRepository especieRepository,
                           InsumoRepository insumoRepository) {
        this.atencionRepository = atencionRepository;
        this.atencionEspecieRepository = atencionEspecieRepository;
        this.atencionInsumoRepository = atencionInsumoRepository;
        this.especieRepository = especieRepository;
        this.insumoRepository = insumoRepository;
    }

    @Transactional(readOnly = true)
    public List<AtencionResponse> listar(TipoAtencion tipo, Integer especieId, Boolean activa) {
        return atencionRepository.findAll(Sort.by("nombre")).stream()
                .filter(a -> tipo == null || a.getTipo() == tipo)
                .filter(a -> activa == null || a.isActiva() == activa)
                .filter(a -> especieId == null || atencionEspecieRepository.existsByAtencion_IdAndEspecie_Id(a.getId(), especieId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AtencionResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public AtencionResponse crear(AtencionRequest request) {
        String nombre = request.getNombre().trim();
        if (atencionRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una atención llamada " + nombre + ".");
        }
        List<Especie> especies = resolverEspecies(request.getEspecieIds());
        validarInsumosRequeridos(request.getInsumosRequeridos());

        Atencion atencion = new Atencion();
        copiarDatos(request, atencion);
        atencion.setActiva(request.getActiva() == null || request.getActiva());
        atencion = atencionRepository.saveAndFlush(atencion);

        guardarRelaciones(atencion, especies, request.getInsumosRequeridos());
        return toResponse(atencion);
    }

    @Transactional
    public AtencionResponse actualizar(Long id, AtencionRequest request) {
        Atencion atencion = buscar(id);
        String nombre = request.getNombre().trim();
        if (atencionRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra atención llamada " + nombre + ".");
        }
        List<Especie> especies = resolverEspecies(request.getEspecieIds());
        validarInsumosRequeridos(request.getInsumosRequeridos());

        copiarDatos(request, atencion);
        if (request.getActiva() != null) {
            atencion.setActiva(request.getActiva());
        }
        atencion = atencionRepository.save(atencion);

        atencionEspecieRepository.deleteAll(atencionEspecieRepository.findByAtencion_Id(id));
        atencionInsumoRepository.deleteAll(atencionInsumoRepository.findByAtencion_Id(id));
        atencionEspecieRepository.flush();
        atencionInsumoRepository.flush();

        guardarRelaciones(atencion, especies, request.getInsumosRequeridos());
        return toResponse(atencion);
    }

    @Transactional
    public AtencionResponse cambiarEstado(Long id, boolean activa) {
        Atencion atencion = buscar(id);
        atencion.setActiva(activa);
        return toResponse(atencionRepository.save(atencion));
    }

    @Transactional(readOnly = true)
    public Atencion buscar(Long id) {
        return atencionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Atención", id));
    }

    private List<Especie> resolverEspecies(List<Integer> especieIds) {
        return new LinkedHashSet<>(especieIds).stream()
                .map(especieId -> especieRepository.findById(especieId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Especie", especieId)))
                .toList();
    }

    private void validarInsumosRequeridos(List<InsumoRequeridoRequest> insumos) {
        if (insumos == null) {
            return;
        }
        Set<Long> vistos = new HashSet<>();
        for (InsumoRequeridoRequest item : insumos) {
            if (!vistos.add(item.getInsumoId())) {
                throw new ReglaNegocioException("El insumo " + item.getInsumoId() + " está repetido en los insumos requeridos.");
            }
            if (!insumoRepository.existsById(item.getInsumoId())) {
                throw new RecursoNoEncontradoException("Insumo", item.getInsumoId());
            }
        }
    }

    private void guardarRelaciones(Atencion atencion, List<Especie> especies, List<InsumoRequeridoRequest> insumos) {
        for (Especie especie : especies) {
            AtencionEspecieId id = new AtencionEspecieId();
            id.setAtencionId(atencion.getId());
            id.setEspecieId(especie.getId());
            AtencionEspecie relacion = new AtencionEspecie();
            relacion.setId(id);
            relacion.setAtencion(atencion);
            relacion.setEspecie(especie);
            atencionEspecieRepository.save(relacion);
        }
        if (insumos == null) {
            return;
        }
        for (InsumoRequeridoRequest item : insumos) {
            Insumo insumo = insumoRepository.getReferenceById(item.getInsumoId());
            AtencionInsumoId id = new AtencionInsumoId();
            id.setAtencionId(atencion.getId());
            id.setInsumoId(item.getInsumoId());
            AtencionInsumo relacion = new AtencionInsumo();
            relacion.setId(id);
            relacion.setAtencion(atencion);
            relacion.setInsumo(insumo);
            relacion.setCantidadRequerida(item.getCantidadRequerida());
            atencionInsumoRepository.save(relacion);
        }
    }

    private void copiarDatos(AtencionRequest request, Atencion atencion) {
        atencion.setTipo(request.getTipo());
        atencion.setNombre(request.getNombre().trim());
        atencion.setDescripcion(request.getDescripcion() == null || request.getDescripcion().isBlank()
                ? null : request.getDescripcion().trim());
        atencion.setPrecioBase(request.getPrecioBase());
        atencion.setDuracionMinutos(request.getDuracionMinutos());
        atencion.setEdadMinimaDias(request.getEdadMinimaDias());
        atencion.setIntervaloMinimoDias(request.getIntervaloMinimoDias());
    }

    private AtencionResponse toResponse(Atencion atencion) {
        List<EspecieResponse> especies = atencionEspecieRepository.findByAtencion_Id(atencion.getId()).stream()
                .map(ae -> EspecieResponse.from(ae.getEspecie()))
                .sorted(Comparator.comparing(EspecieResponse::nombre))
                .toList();
        List<AtencionResponse.InsumoRequeridoResponse> insumos = atencionInsumoRepository.findByAtencion_Id(atencion.getId())
                .stream()
                .map(ai -> new AtencionResponse.InsumoRequeridoResponse(
                        ai.getInsumo().getId(), ai.getInsumo().getNombre(), ai.getCantidadRequerida()))
                .toList();
        return new AtencionResponse(
                atencion.getId(),
                atencion.getTipo(),
                atencion.getNombre(),
                atencion.getDescripcion(),
                atencion.getPrecioBase(),
                atencion.getDuracionMinutos(),
                atencion.getEdadMinimaDias(),
                atencion.getIntervaloMinimoDias(),
                atencion.isActiva(),
                especies,
                insumos
        );
    }
}
