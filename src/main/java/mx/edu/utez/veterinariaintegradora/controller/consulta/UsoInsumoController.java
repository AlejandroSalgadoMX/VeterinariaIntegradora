package mx.edu.utez.veterinariaintegradora.controller.consulta;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.consulta.UsoInsumoRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.UsoInsumoResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.consulta.UsoInsumoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-VET-03 · Uso de insumos (lotes) en la cita */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/citas/{citaId}")
@RolesPermitidos(RolUsuario.VETERINARIO)
public class UsoInsumoController {

    private final UsoInsumoService usoInsumoService;

    public UsoInsumoController(UsoInsumoService usoInsumoService) {
        this.usoInsumoService = usoInsumoService;
    }

    @GetMapping("/insumos")
    public List<UsoInsumoResponse> listar(@PathVariable Long citaId) {
        return usoInsumoService.listarPorCita(citaId);
    }

    @PostMapping("/atenciones/{citaAtencionId}/insumos")
    public ResponseEntity<UsoInsumoResponse> registrar(@PathVariable Long citaId,
                                                       @PathVariable Long citaAtencionId,
                                                       @Valid @RequestBody UsoInsumoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usoInsumoService.registrar(citaId, citaAtencionId, request));
    }
}
