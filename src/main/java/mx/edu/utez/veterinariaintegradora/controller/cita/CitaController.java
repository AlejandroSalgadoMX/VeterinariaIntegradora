package mx.edu.utez.veterinariaintegradora.controller.cita;

import jakarta.validation.Valid;
import mx.edu.utez.veterinariaintegradora.dto.cita.CitaRequest;
import mx.edu.utez.veterinariaintegradora.dto.cita.CitaResponse;
import mx.edu.utez.veterinariaintegradora.dto.cita.EstadoCitaRequest;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.security.RolesPermitidos;
import mx.edu.utez.veterinariaintegradora.service.cita.CitaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** HU-REC-03 · Agenda de citas */
@RestController
@CrossOrigin({"*"})
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<CitaResponse> listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                     @RequestParam(required = false) Long veterinarioId,
                                     @RequestParam(required = false) Long mascotaId,
                                     @RequestParam(required = false) EstadoCita estado) {
        return citaService.listar(fecha, veterinarioId, mascotaId, estado);
    }

    @GetMapping("/{id}")
    public CitaResponse obtener(@PathVariable Long id) {
        return citaService.obtener(id);
    }

    @RolesPermitidos(RolUsuario.RECEPCION)
    @PostMapping
    public ResponseEntity<CitaResponse> agendar(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.agendar(request));
    }

    @RolesPermitidos(RolUsuario.RECEPCION)
    @PutMapping("/{id}")
    public CitaResponse reprogramar(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return citaService.reprogramar(id, request);
    }

    @RolesPermitidos({RolUsuario.RECEPCION, RolUsuario.VETERINARIO})
    @PatchMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoCitaRequest request) {
        return citaService.cambiarEstado(id, request.getEstado());
    }
}
