package mx.edu.utez.veterinariaintegradora.service.inventario;

import mx.edu.utez.veterinariaintegradora.dto.inventario.InsumoRequest;
import mx.edu.utez.veterinariaintegradora.dto.inventario.InsumoResponse;
import mx.edu.utez.veterinariaintegradora.dto.inventario.LoteRequest;
import mx.edu.utez.veterinariaintegradora.dto.inventario.LoteResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.inventario.Insumo;
import mx.edu.utez.veterinariaintegradora.model.inventario.LoteInsumo;
import mx.edu.utez.veterinariaintegradora.repository.cita.UsoInsumoRepository;
import mx.edu.utez.veterinariaintegradora.repository.inventario.InsumoRepository;
import mx.edu.utez.veterinariaintegradora.repository.inventario.LoteInsumoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * HU-ADM-04 · Insumos y lotes. La existencia de cada lote se calcula como
 * cantidad ingresada - usos registrados (igual que la vista vw_existencia_lote).
 */
@Service
public class InventarioService {

    private final InsumoRepository insumoRepository;
    private final LoteInsumoRepository loteInsumoRepository;
    private final UsoInsumoRepository usoInsumoRepository;

    public InventarioService(InsumoRepository insumoRepository,
                             LoteInsumoRepository loteInsumoRepository,
                             UsoInsumoRepository usoInsumoRepository) {
        this.insumoRepository = insumoRepository;
        this.loteInsumoRepository = loteInsumoRepository;
        this.usoInsumoRepository = usoInsumoRepository;
    }

    // ===== Insumos =====

    @Transactional(readOnly = true)
    public List<InsumoResponse> listarInsumos(Boolean activo) {
        return insumoRepository.findAll(Sort.by("nombre")).stream()
                .filter(i -> activo == null || i.isActivo() == activo)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InsumoResponse obtenerInsumo(Long id) {
        return toResponse(buscarInsumo(id));
    }

    @Transactional
    public InsumoResponse crearInsumo(InsumoRequest request) {
        String nombre = request.getNombre().trim();
        if (insumoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe un insumo llamado " + nombre + ".");
        }
        Insumo insumo = new Insumo();
        copiarDatos(request, insumo);
        insumo.setActivo(request.getActivo() == null || request.getActivo());
        return toResponse(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoResponse actualizarInsumo(Long id, InsumoRequest request) {
        Insumo insumo = buscarInsumo(id);
        String nombre = request.getNombre().trim();
        if (insumoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otro insumo llamado " + nombre + ".");
        }
        copiarDatos(request, insumo);
        if (request.getActivo() != null) {
            insumo.setActivo(request.getActivo());
        }
        return toResponse(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoResponse cambiarEstadoInsumo(Long id, boolean activo) {
        Insumo insumo = buscarInsumo(id);
        insumo.setActivo(activo);
        return toResponse(insumoRepository.save(insumo));
    }

    @Transactional(readOnly = true)
    public Insumo buscarInsumo(Long id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo", id));
    }

    // ===== Lotes =====

    @Transactional(readOnly = true)
    public List<LoteResponse> listarLotes(Long insumoId, Boolean soloVigentes) {
        buscarInsumo(insumoId);
        LocalDate hoy = LocalDate.now();
        return loteInsumoRepository.findByInsumo_IdOrderByFechaCaducidadAscIdAsc(insumoId).stream()
                .filter(l -> soloVigentes == null || !soloVigentes || !l.getFechaCaducidad().isBefore(hoy))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoteResponse obtenerLote(Long loteId) {
        return toResponse(buscarLote(loteId));
    }

    @Transactional
    public LoteResponse registrarLote(Long insumoId, LoteRequest request) {
        Insumo insumo = buscarInsumo(insumoId);
        if (!insumo.isActivo()) {
            throw new ReglaNegocioException("No se pueden registrar entradas de un insumo inactivo.");
        }
        if (request.getFechaCaducidad().isBefore(request.getFechaIngreso())) {
            throw new ReglaNegocioException("La fecha de caducidad no puede ser anterior a la fecha de ingreso.");
        }
        String codigo = request.getCodigoLote().trim();
        if (loteInsumoRepository.existsByInsumo_IdAndCodigoLoteIgnoreCase(insumoId, codigo)) {
            throw new ReglaNegocioException("El lote " + codigo + " ya fue registrado para este insumo.");
        }
        LoteInsumo lote = new LoteInsumo();
        lote.setInsumo(insumo);
        lote.setCodigoLote(codigo);
        lote.setUnidadMedida(request.getUnidadMedida());
        lote.setCantidadIngresada(request.getCantidadIngresada());
        lote.setFechaIngreso(request.getFechaIngreso());
        lote.setFechaCaducidad(request.getFechaCaducidad());
        return toResponse(loteInsumoRepository.save(lote));
    }

    /** Lotes con existencia que caducan dentro de los próximos días indicados. */
    @Transactional(readOnly = true)
    public List<LoteResponse> lotesPorCaducar(int dias) {
        LocalDate hoy = LocalDate.now();
        return loteInsumoRepository.findByFechaCaducidadBetweenOrderByFechaCaducidadAsc(hoy, hoy.plusDays(dias))
                .stream()
                .map(this::toResponse)
                .filter(l -> l.existenciaDisponible().signum() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoteInsumo buscarLote(Long loteId) {
        return loteInsumoRepository.findById(loteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de insumo", loteId));
    }

    // ===== Reglas para descontar inventario (usadas por la atención clínica) =====

    public BigDecimal existenciaDisponible(LoteInsumo lote) {
        BigDecimal usado = usoInsumoRepository.sumarCantidadUsadaPorLote(lote.getId());
        return lote.getCantidadIngresada().subtract(usado == null ? BigDecimal.ZERO : usado);
    }

    /**
     * Bloquea y valida un lote elegido: debe ser del insumo esperado (si se indica),
     * no estar caducado y tener existencia suficiente.
     */
    @Transactional
    public LoteInsumo reservarLote(Long loteId, Long insumoEsperadoId, BigDecimal cantidad) {
        LoteInsumo lote = loteInsumoRepository.findByIdParaUso(loteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de insumo", loteId));
        Insumo insumo = lote.getInsumo();
        if (insumoEsperadoId != null && !insumo.getId().equals(insumoEsperadoId)) {
            throw new ReglaNegocioException("El lote " + lote.getCodigoLote() + " no corresponde al insumo requerido.");
        }
        if (!insumo.isActivo()) {
            throw new ReglaNegocioException("El insumo " + insumo.getNombre() + " está inactivo.");
        }
        if (lote.getFechaCaducidad().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("El lote " + lote.getCodigoLote() + " de " + insumo.getNombre()
                    + " caducó el " + lote.getFechaCaducidad() + "; no se puede usar.");
        }
        BigDecimal disponible = existenciaDisponible(lote);
        if (disponible.compareTo(cantidad) < 0) {
            throw new ReglaNegocioException("Existencia insuficiente en el lote " + lote.getCodigoLote()
                    + " de " + insumo.getNombre() + ": disponible " + disponible + ", requerido " + cantidad + ".");
        }
        return lote;
    }

    /** Elige automáticamente el lote vigente que caduca primero y alcanza para la cantidad (FEFO). */
    @Transactional
    public LoteInsumo reservarLoteVigente(Insumo insumo, BigDecimal cantidad) {
        if (!insumo.isActivo()) {
            throw new ReglaNegocioException("El insumo " + insumo.getNombre() + " está inactivo.");
        }
        return loteInsumoRepository.findVigentesParaUso(insumo.getId(), LocalDate.now()).stream()
                .filter(lote -> existenciaDisponible(lote).compareTo(cantidad) >= 0)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No hay un lote vigente de " + insumo.getNombre()
                        + " con existencia suficiente (" + cantidad + "); la atención no puede registrarse."));
    }

    // ===== Mapeo =====

    private void copiarDatos(InsumoRequest request, Insumo insumo) {
        insumo.setNombre(request.getNombre().trim());
        insumo.setDescripcion(request.getDescripcion() == null || request.getDescripcion().isBlank()
                ? null : request.getDescripcion().trim());
        insumo.setPrecioVentaBase(request.getPrecioVentaBase());
    }

    private InsumoResponse toResponse(Insumo insumo) {
        LocalDate hoy = LocalDate.now();
        List<LoteResponse> vigentes = loteInsumoRepository.findByInsumo_IdOrderByFechaCaducidadAscIdAsc(insumo.getId())
                .stream()
                .filter(l -> !l.getFechaCaducidad().isBefore(hoy))
                .map(this::toResponse)
                .toList();
        BigDecimal existencia = vigentes.stream()
                .map(LoteResponse::existenciaDisponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int lotesConExistencia = (int) vigentes.stream().filter(l -> l.existenciaDisponible().signum() > 0).count();
        return new InsumoResponse(insumo.getId(), insumo.getNombre(), insumo.getDescripcion(),
                insumo.getPrecioVentaBase(), insumo.isActivo(), existencia, lotesConExistencia);
    }

    private LoteResponse toResponse(LoteInsumo lote) {
        BigDecimal usado = usoInsumoRepository.sumarCantidadUsadaPorLote(lote.getId());
        usado = usado == null ? BigDecimal.ZERO : usado;
        return new LoteResponse(
                lote.getId(),
                lote.getInsumo().getId(),
                lote.getInsumo().getNombre(),
                lote.getCodigoLote(),
                lote.getUnidadMedida(),
                lote.getCantidadIngresada(),
                usado,
                lote.getCantidadIngresada().subtract(usado),
                lote.getFechaIngreso(),
                lote.getFechaCaducidad(),
                lote.getFechaCaducidad().isBefore(LocalDate.now())
        );
    }
}
