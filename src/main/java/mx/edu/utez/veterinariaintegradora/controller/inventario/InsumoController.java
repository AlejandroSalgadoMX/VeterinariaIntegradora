package mx.edu.utez.veterinariaintegradora.controller.inventario;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.comun.EstadoActivoRequest;
import mx.edu.utez.veterinariaintegradora.dto.inventario.InsumoRequest;
import mx.edu.utez.veterinariaintegradora.dto.inventario.InsumoResponse;
import mx.edu.utez.veterinariaintegradora.dto.inventario.LoteRequest;
import mx.edu.utez.veterinariaintegradora.dto.inventario.LoteResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.inventario.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-ADM-04 · Insumos y lotes. El veterinario puede consultarlos para elegir lote. */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/insumos")
@RolesPermitidos({RolUsuario.ADMINISTRADOR, RolUsuario.VETERINARIO})
public class InsumoController {

    private final InventarioService inventarioService;

    public InsumoController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<InsumoResponse> listar(@RequestParam(required = false) Boolean activo) {
        return inventarioService.listarInsumos(activo);
    }

    @GetMapping("/{id}")
    public InsumoResponse obtener(@PathVariable Long id) {
        return inventarioService.obtenerInsumo(id);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<InsumoResponse> crear(@Valid @RequestBody InsumoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearInsumo(request));
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PutMapping("/{id}")
    public InsumoResponse actualizar(@PathVariable Long id, @Valid @RequestBody InsumoRequest request) {
        return inventarioService.actualizarInsumo(id, request);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PatchMapping("/{id}/estado")
    public InsumoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest request) {
        return inventarioService.cambiarEstadoInsumo(id, request.getActivo());
    }

    @GetMapping("/{id}/lotes")
    public List<LoteResponse> listarLotes(@PathVariable Long id,
                                          @RequestParam(required = false) Boolean soloVigentes) {
        return inventarioService.listarLotes(id, soloVigentes);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PostMapping("/{id}/lotes")
    public ResponseEntity<LoteResponse> registrarLote(@PathVariable Long id, @Valid @RequestBody LoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.registrarLote(id, request));
    }

    @GetMapping("/lotes/{loteId}")
    public LoteResponse obtenerLote(@PathVariable Long loteId) {
        return inventarioService.obtenerLote(loteId);
    }

    @GetMapping("/lotes/por-caducar")
    public List<LoteResponse> lotesPorCaducar(@RequestParam(defaultValue = "30") int dias) {
        return inventarioService.lotesPorCaducar(dias);
    }
}
