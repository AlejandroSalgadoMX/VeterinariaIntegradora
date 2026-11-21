package mx.edu.utez.veterinariaintegradora.dto.consulta;

import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import mx.edu.utez.veterinariaintegradora.model.inventario.UnidadMedida;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsoInsumoResponse(
        Long id,
        Long citaAtencionId,
        Long insumoId,
        String insumoNombre,
        Long loteInsumoId,
        String codigoLote,
        LocalDate fechaCaducidadLote,
        UnidadMedida unidadMedida,
        BigDecimal cantidadUsada,
        BigDecimal precioCobradoUnitario,
        BigDecimal subtotal,
        LocalDateTime usadoEn
) {

    public static UsoInsumoResponse from(UsoInsumo u) {
        return new UsoInsumoResponse(
                u.getId(),
                u.getCitaAtencion().getId(),
                u.getLoteInsumo().getInsumo().getId(),
                u.getLoteInsumo().getInsumo().getNombre(),
                u.getLoteInsumo().getId(),
                u.getLoteInsumo().getCodigoLote(),
                u.getLoteInsumo().getFechaCaducidad(),
                u.getLoteInsumo().getUnidadMedida(),
                u.getCantidadUsada(),
                u.getPrecioCobradoUnitario(),
                u.getCantidadUsada().multiply(u.getPrecioCobradoUnitario()).setScale(2, RoundingMode.HALF_UP),
                u.getUsadoEn()
        );
    }
}
