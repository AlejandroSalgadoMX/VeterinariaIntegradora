package mx.edu.utez.veterinariaintegradora.dto.cobro;

import mx.edu.utez.veterinariaintegradora.model.cobro.FormaPago;
import mx.edu.utez.veterinariaintegradora.model.cobro.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long cargoId,
        FormaPago formaPago,
        BigDecimal montoPagado,
        LocalDateTime fechaPago
) {

    public static PagoResponse from(Pago pago) {
        return new PagoResponse(pago.getId(), pago.getCargo().getId(), pago.getFormaPago(),
                pago.getMontoPagado(), pago.getFechaPago());
    }
}
