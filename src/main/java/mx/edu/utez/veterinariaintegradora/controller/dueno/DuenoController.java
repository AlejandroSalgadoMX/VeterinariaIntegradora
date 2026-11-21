package mx.edu.utez.veterinariaintegradora.controller.dueno;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoRequest;
import mx.edu.utez.veterinariaintegradora.dto.dueno.DuenoResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.dueno.DuenoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-REC-01 · Dueños */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/duenos")
@RolesPermitidos(RolUsuario.RECEPCION)
public class DuenoController {

    private final DuenoService duenoService;

    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping
    public List<DuenoResponse> listar(@RequestParam(name = "q", required = false) String busqueda) {
        return duenoService.listar(busqueda);
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping("/{id}")
    public DuenoResponse obtener(@PathVariable Long id) {
        return duenoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<DuenoResponse> crear(@Valid @RequestBody DuenoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(duenoService.crear(request));
    }

    @PutMapping("/{id}")
    public DuenoResponse actualizar(@PathVariable Long id, @Valid @RequestBody DuenoRequest request) {
        return duenoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        duenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
