package mx.edu.utez.veterinariaintegradora.controller.cobro;

import mx.edu.utez.veterinariaintegradora.dto.cobro.CargoResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.cobro.CobroService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/citas/{citaId}")
public class CargoController {

    private final CobroService cobroService;

    public CargoController(CobroService cobroService) {
        this.cobroService = cobroService;
    }

    /** Termina la documentación de la visita: genera el cargo y la cita queda POR_COBRAR. */
    @RolesPermitidos(RolUsuario.VETERINARIO)
    @PostMapping("/finalizar")
    public CargoResponse finalizar(@PathVariable Long citaId) {
        return cobroService.finalizarAtencion(citaId);
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @GetMapping("/cargo")
    public CargoResponse obtenerCargo(@PathVariable Long citaId) {
        return cobroService.obtenerCargo(citaId);
    }
}
