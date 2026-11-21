package mx.edu.utez.veterinariaintegradora.controller.atencion;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.atencion.AtencionRequest;
import mx.edu.utez.veterinariaintegradora.dto.atencion.AtencionResponse;
import mx.edu.utez.veterinariaintegradora.dto.comun.EstadoActivoRequest;
import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.atencion.AtencionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-ADM-03 · Catálogo de atenciones. Consulta abierta; altas y cambios solo administrador. */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    @GetMapping
    public List<AtencionResponse> listar(@RequestParam(required = false) TipoAtencion tipo,
                                         @RequestParam(required = false) Integer especieId,
                                         @RequestParam(required = false) Boolean activa) {
        return atencionService.listar(tipo, especieId, activa);
    }

    @GetMapping("/{id}")
    public AtencionResponse obtener(@PathVariable Long id) {
        return atencionService.obtener(id);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<AtencionResponse> crear(@Valid @RequestBody AtencionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atencionService.crear(request));
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PutMapping("/{id}")
    public AtencionResponse actualizar(@PathVariable Long id, @Valid @RequestBody AtencionRequest request) {
        return atencionService.actualizar(id, request);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PatchMapping("/{id}/estado")
    public AtencionResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest request) {
        return atencionService.cambiarEstado(id, request.getActivo());
    }
}
