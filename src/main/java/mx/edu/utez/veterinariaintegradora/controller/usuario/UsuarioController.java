package mx.edu.utez.veterinariaintegradora.controller.usuario;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.comun.EstadoActivoRequest;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioActualizarRequest;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioCrearRequest;
import mx.edu.utez.veterinariaintegradora.dto.usuario.UsuarioResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.usuario.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-ADM-01 · Usuarios del sistema */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/usuarios")
@RolesPermitidos(RolUsuario.ADMINISTRADOR)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) RolUsuario rol,
                                        @RequestParam(required = false) Boolean activo) {
        return usuarioService.listar(rol, activo);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCrearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequest request) {
        return usuarioService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoActivoRequest request) {
        return usuarioService.cambiarEstado(id, request.getActivo());
    }
}
