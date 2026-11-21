package mx.edu.utez.veterinariaintegradora.service.mascota;

import mx.edu.utez.veterinariaintegradora.model.mascota.Mascota;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calcula la edad de la mascota en días para validar la edad mínima de las atenciones.
 * Si solo hay edad aproximada, se toma la edad registrada (meses x 30) más los días
 * transcurridos desde su registro.
 */
public final class EdadMascota {

    private static final int DIAS_POR_MES = 30;

    private EdadMascota() {
    }

    public static long enDias(Mascota mascota, LocalDate fecha) {
        if (mascota.getFechaNacimiento() != null) {
            return Math.max(0, ChronoUnit.DAYS.between(mascota.getFechaNacimiento(), fecha));
        }
        if (mascota.getEdadAproximadaMeses() == null) {
            return 0;
        }
        LocalDate registro = mascota.getCreadoEn() != null ? mascota.getCreadoEn().toLocalDate() : fecha;
        long diasDesdeRegistro = Math.max(0, ChronoUnit.DAYS.between(registro, fecha));
        return (long) mascota.getEdadAproximadaMeses() * DIAS_POR_MES + diasDesdeRegistro;
    }
}
