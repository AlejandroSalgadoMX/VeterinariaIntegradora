package mx.edu.utez.veterinariaintegradora.controller.consulta;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.consulta.AtencionAplicadaRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.CitaAtencionResponse;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.consulta.AtencionClinicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** HU-VET-02 · Atenciones aplicadas en la cita */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/citas/{citaId}/atenciones")
@RolesPermitidos(RolUsuario.VETERINARIO)
public class AtencionClinicaController {

    private final AtencionClinicaService atencionClinicaService;

    public AtencionClinicaController(AtencionClinicaService atencionClinicaService) {
        this.atencionClinicaService = atencionClinicaService;
    }

    @RolesPermitidos({RolUsuario.VETERINARIO, RolUsuario.RECEPCION})
    @GetMapping
    public List<CitaAtencionResponse> listar(@PathVariable Long citaId) {
        return atencionClinicaService.listar(citaId);
    }

    @PostMapping
    public ResponseEntity<CitaAtencionResponse> registrar(@PathVariable Long citaId,
                                                          @Valid @RequestBody AtencionAplicadaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atencionClinicaService.registrar(citaId, request));
    }

    @DeleteMapping("/{citaAtencionId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long citaId, @PathVariable Long citaAtencionId) {
        atencionClinicaService.eliminar(citaId, citaAtencionId);
        return ResponseEntity.noContent().build();
    }
}
