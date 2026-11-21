package mx.edu.utez.veterinariaintegradora.service.consulta;

import mx.edu.utez.veterinariaintegradora.dto.consulta.UsoInsumoRequest;
import mx.edu.utez.veterinariaintegradora.dto.consulta.UsoInsumoResponse;
import mx.edu.utez.veterinariaintegradora.model.cita.Cita;
import mx.edu.utez.veterinariaintegradora.model.cita.CitaAtencion;
import mx.edu.utez.veterinariaintegradora.model.cita.UsoInsumo;
import mx.edu.utez.veterinariaintegradora.model.inventario.LoteInsumo;
import mx.edu.utez.veterinariaintegradora.repository.cita.UsoInsumoRepository;
import mx.edu.utez.veterinariaintegradora.service.cita.CitaService;
import mx.edu.utez.veterinariaintegradora.service.inventario.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * HU-VET-03 · Uso de insumos durante la atención: descuenta de un lote vigente,
 * sin usar lotes caducados ni dejar existencia negativa.
 */
@Service
public class UsoInsumoService {

    private final CitaService citaService;
    private final AtencionClinicaService atencionClinicaService;
    private final InventarioService inventarioService;
    private final UsoInsumoRepository usoInsumoRepository;

    public UsoInsumoService(CitaService citaService,
                            AtencionClinicaService atencionClinicaService,
                            InventarioService inventarioService,
                            UsoInsumoRepository usoInsumoRepository) {
        this.citaService = citaService;
        this.atencionClinicaService = atencionClinicaService;
        this.inventarioService = inventarioService;
        this.usoInsumoRepository = usoInsumoRepository;
    }

    @Transactional(readOnly = true)
    public List<UsoInsumoResponse> listarPorCita(Long citaId) {
        citaService.buscar(citaId);
        return usoInsumoRepository.findByCitaAtencion_Cita_IdOrderByUsadoEnAsc(citaId).stream()
                .map(UsoInsumoResponse::from)
                .toList();
    }

    @Transactional
    public UsoInsumoResponse registrar(Long citaId, Long citaAtencionId, UsoInsumoRequest request) {
        Cita cita = citaService.buscar(citaId);
        atencionClinicaService.validarCitaEnAtencion(cita);
        CitaAtencion citaAtencion = atencionClinicaService.buscarDeCita(citaId, citaAtencionId);

        LoteInsumo lote = inventarioService.reservarLote(request.getLoteInsumoId(), null, request.getCantidadUsada());
        boolean cobrable = request.getCobrable() == null || request.getCobrable();

        UsoInsumo uso = new UsoInsumo();
        uso.setCitaAtencion(citaAtencion);
        uso.setLoteInsumo(lote);
        uso.setCantidadUsada(request.getCantidadUsada());
        uso.setPrecioCobradoUnitario(cobrable ? lote.getInsumo().getPrecioVentaBase() : BigDecimal.ZERO);
        return UsoInsumoResponse.from(usoInsumoRepository.save(uso));
    }
}
