package mx.edu.utez.veterinariaintegradora.dto.atencion;

import mx.edu.utez.veterinariaintegradora.dto.especie.EspecieResponse;
import mx.edu.utez.veterinariaintegradora.model.atencion.TipoAtencion;

import java.math.BigDecimal;
import java.util.List;

public record AtencionResponse(
        Long id,
        TipoAtencion tipo,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        Integer duracionMinutos,
        Integer edadMinimaDias,
        Integer intervaloMinimoDias,
        boolean activa,
        List<EspecieResponse> especies,
        List<InsumoRequeridoResponse> insumosRequeridos
) {

    public record InsumoRequeridoResponse(Long insumoId, String insumoNombre, BigDecimal cantidadRequerida) {
    }
}
