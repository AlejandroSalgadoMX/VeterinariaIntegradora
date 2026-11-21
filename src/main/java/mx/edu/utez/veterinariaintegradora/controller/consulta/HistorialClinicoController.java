package mx.edu.utez.veterinariaintegradora.controller.consulta;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.consulta.HistorialClinicoService;
import org.springframework.web.bind.annotation.*;

/** HU-VET-01 · Historial clínico de la cita */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/citas/{citaId}/historial")
@RolesPermitidos(RolUsuario.VETERINARIO)
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    public HistorialClinicoController(HistorialClinicoService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public HistorialResponse obtener(@PathVariable Long citaId) {
        return historialService.obtener(citaId);
    }

    @PutMapping
    public HistorialResponse registrar(@PathVariable Long citaId, @Valid @RequestBody HistorialRequest request) {
        return historialService.registrar(citaId, request);
    }
}
