package mx.edu.utez.veterinariaintegradora.dto.especie;

import mx.edu.utez.veterinariaintegradora.model.especie.Especie;

public record EspecieResponse(Integer id, String nombre) {

    public static EspecieResponse from(Especie especie) {
        return new EspecieResponse(especie.getId(), especie.getNombre());
    }
}
