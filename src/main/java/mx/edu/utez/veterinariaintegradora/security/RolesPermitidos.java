package mx.edu.utez.veterinariaintegradora.security;

import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indica qué roles pueden usar un endpoint (método o controlador completo).
 * El ADMINISTRADOR siempre tiene acceso. Si no se anota, basta con estar autenticado.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RolesPermitidos {

    RolUsuario[] value();
}
