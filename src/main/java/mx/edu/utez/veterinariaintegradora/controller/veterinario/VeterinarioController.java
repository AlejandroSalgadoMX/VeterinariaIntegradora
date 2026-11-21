package mx.edu.utez.veterinariaintegradora.controller.veterinario;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.comun.EstadoActivoRequest;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioRequest;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.veterinario.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-ADM-02 · Veterinarios. Consulta abierta a cualquier usuario autenticado (recepción agenda con ellos). */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public List<VeterinarioResponse> listar(@RequestParam(required = false) Boolean activo,
                                            @RequestParam(required = false) String especialidad) {
        return veterinarioService.listar(activo, especialidad);
    }

    @GetMapping("/{id}")
    public VeterinarioResponse obtener(@PathVariable Long id) {
        return veterinarioService.obtener(id);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<VeterinarioResponse> crear(@Valid @RequestBody VeterinarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.crear(request));
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PutMapping("/{id}")
    public VeterinarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequest request) {
        return veterinarioService.actualizar(id, request);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PatchMapping("/{id}/estado")
    public VeterinarioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest request) {
        return veterinarioService.cambiarEstado(id, request.getActivo());
    }
}
