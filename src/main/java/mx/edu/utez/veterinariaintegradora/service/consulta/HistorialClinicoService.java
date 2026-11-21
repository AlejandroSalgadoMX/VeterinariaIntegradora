package mx.edu.utez.veterinariaintegradora.service.consulta;

import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.HistorialResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.EstadoCita;
import mx.edu.utez.veterinariaintegradora.model.cita.HistorialClinico;
import mx.edu.utez.veterinariaintegradora.repository.cita.HistorialClinicoRepository;
import mx.edu.utez.veterinariaintegradora.service.cita.CitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** HU-VET-01 · Historial clínico: nota por cita (peso, diagnóstico, indicaciones y observaciones). */
@Service
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;
    private final CitaService citaService;

    public HistorialClinicoService(HistorialClinicoRepository historialRepository, CitaService citaService) {
        this.historialRepository = historialRepository;
        this.citaService = citaService;
    }

    @Transactional(readOnly = true)
    public HistorialResponse obtener(Long citaId) {
        citaService.buscar(citaId);
        return historialRepository.findByCita_Id(citaId)
                .map(HistorialResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Historial clínico de la cita", citaId));
    }

    /** Crea o actualiza la nota clínica mientras la cita está EN_ATENCION. */
    @Transactional
    public HistorialResponse registrar(Long citaId, HistorialRequest request) {
        Cita cita = citaService.buscar(citaId);
        if (cita.getEstado() != EstadoCita.EN_ATENCION) {
            throw new ReglaNegocioException("Solo se registran notas clínicas con la cita EN_ATENCION (estado actual: "
                    + cita.getEstado() + ").");
        }
        if (request.getPesoKg() == null && vacio(request.getDiagnostico())
                && vacio(request.getIndicaciones()) && vacio(request.getObservaciones())) {
            throw new ReglaNegocioException("La nota clínica debe incluir al menos peso, diagnóstico, indicaciones u observaciones.");
        }
        HistorialClinico historial = historialRepository.findByCita_Id(citaId).orElseGet(() -> {
            HistorialClinico nuevo = new HistorialClinico();
            nuevo.setCita(cita);
            return nuevo;
        });
        historial.setPesoKg(request.getPesoKg());
        historial.setDiagnostico(limpiar(request.getDiagnostico()));
        historial.setIndicaciones(limpiar(request.getIndicaciones()));
        historial.setObservaciones(limpiar(request.getObservaciones()));
        return HistorialResponse.from(historialRepository.save(historial));
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String limpiar(String valor) {
        return vacio(valor) ? null : valor.trim();
    }
}
