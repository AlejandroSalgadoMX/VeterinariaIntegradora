package mx.edu.utez.veterinariaintegradora.config;

import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;
import mx.edu.utez.veterinariaintegradora.repository.usuario.UsuarioRepository;
import mx.edu.utez.veterinariaintegradora.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Crea un administrador inicial cuando la tabla de usuarios está vacía. */
@Configuration
public class DatosInicialesConfig {

    private static final Logger log = LoggerFactory.getLogger(DatosInicialesConfig.class);

    @Bean
    CommandLineRunner crearAdministradorInicial(UsuarioRepository usuarioRepository,
                                                PasswordHasher passwordHasher,
                                                @Value("${app.admin.correo:admin@veterinaria.mx}") String correo,
                                                @Value("${app.admin.contrasena:Admin1234}") String contrasena) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }
            Usuario admin = new Usuario();
            admin.setRol(RolUsuario.ADMINISTRADOR);
            admin.setCorreo(correo.trim().toLowerCase());
            admin.setContrasenaHash(passwordHasher.hash(contrasena));
            admin.setActivo(true);
            usuarioRepository.save(admin);
            log.info("Administrador inicial creado: {} (cambia la contraseña después del primer acceso)", correo);
        };
    }
}
