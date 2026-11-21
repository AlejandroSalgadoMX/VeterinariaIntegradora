package mx.edu.utez.veterinariaintegradora.controller.mascota;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.mascota.EstadoMascotaRequest;
import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaRequest;
import mx.edu.utez.veterinariaintegradora.dto.mascota.MascotaResponse;
import mx.edu.utez.veterinariaintegradora.model.mascota.EstadoMascota;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.mascota.MascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-REC-02 · Mascotas */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api")
@RolesPermitidos(RolUsuario.RECEPCION)
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping("/mascotas")
    public List<MascotaResponse> listar(@RequestParam(required = false) Long duenoId,
                                        @RequestParam(required = false) Integer especieId,
                                        @RequestParam(required = false) EstadoMascota estado,
                                        @RequestParam(required = false) String nombre) {
        return mascotaService.listar(duenoId, especieId, estado, nombre);
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping("/mascotas/{id}")
    public MascotaResponse obtener(@PathVariable Long id) {
        return mascotaService.obtener(id);
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping("/duenos/{duenoId}/mascotas")
    public List<MascotaResponse> listarPorDueno(@PathVariable Long duenoId) {
        return mascotaService.listarPorDueno(duenoId);
    }

    @PostMapping("/mascotas")
    public ResponseEntity<MascotaResponse> crear(@Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.crear(request));
    }

    @PutMapping("/mascotas/{id}")
    public MascotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
        return mascotaService.actualizar(id, request);
    }

    @PatchMapping("/mascotas/{id}/estado")
    public MascotaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoMascotaRequest request) {
        return mascotaService.cambiarEstado(id, request.getEstado());
    }
}
