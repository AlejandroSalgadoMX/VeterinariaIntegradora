package mx.edu.utez.veterinariaintegradora.controller.expediente;

import mx.edu.utez.veterinariaintegradora.dto.expediente.ExpedienteResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.expediente.ExpedienteService;
import org.springframework.web.bind.annotation.*;

/** HU-VET-04 · Consulta de expediente (solo lectura) */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/mascotas/{mascotaId}/expediente")
@RolesPermitidos(RolUsuario.VETERINARIO)
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    public ExpedienteController(ExpedienteService expedienteService) {
        this.expedienteService = expedienteService;
    }

    @GetMapping
    public ExpedienteResponse obtener(@PathVariable Long mascotaId) {
        return expedienteService.obtener(mascotaId);
    }
}
