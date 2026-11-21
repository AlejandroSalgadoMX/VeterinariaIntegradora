package mx.edu.utez.veterinariaintegradora.controller.cobro;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.cobro.CargoResponse;
import mx.edu.utez.veterinariaintegradora.dto.cobro.CitaPorCobrarResponse;
import mx.edu.utez.veterinariaintegradora.dto.cobro.PagoRequest;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.cobro.CobroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-REC-04 · Cobros y cierre de cita */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api")
@RolesPermitidos(RolUsuario.RECEPCION)
public class CobroController {

    private final CobroService cobroService;

    public CobroController(CobroService cobroService) {
        this.cobroService = cobroService;
    }

    @GetMapping("/cobros/pendientes")
    public List<CitaPorCobrarResponse> pendientes() {
        return cobroService.listarPendientes();
    }

    @PostMapping("/citas/{citaId}/pago")
    public ResponseEntity<CargoResponse> pagar(@PathVariable Long citaId, @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cobroService.registrarPago(citaId, request));
    }
}
