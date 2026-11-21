package mx.edu.utez.veterinariaintegradora.controller.especie;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieRequest;
import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.especie.EspecieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @GetMapping
    public List<EspecieResponse> listar() {
        return especieService.listar();
    }

    @GetMapping("/{id}")
    public EspecieResponse obtener(@PathVariable Integer id) {
        return especieService.obtener(id);
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<EspecieResponse> crear(@Valid @RequestBody EspecieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(especieService.crear(request));
    }

    @RolesPermitidos(RolUsuario.ADMINISTRADOR)
    @PutMapping("/{id}")
    public EspecieResponse actualizar(@PathVariable Integer id, @Valid @RequestBody EspecieRequest request) {
        return especieService.actualizar(id, request);
    }
}
