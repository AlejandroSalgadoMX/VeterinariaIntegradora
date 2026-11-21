package mx.edu.utez.veterinariaintegradora.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Genera y verifica hashes de contraseña con PBKDF2 (incluido en el JDK),
 * con formato: pbkdf2$iteraciones$salBase64$hashBase64
 */
@Component
public class PasswordHasher {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_BITS = 256;
    private static final int LONGITUD_SAL = 16;

    private final SecureRandom random = new SecureRandom();

    public String hash(String contrasena) {
        byte[] sal = new byte[LONGITUD_SAL];
        random.nextBytes(sal);
        byte[] hash = derivar(contrasena.toCharArray(), sal, ITERACIONES);
        Base64.Encoder encoder = Base64.getEncoder();
        return "pbkdf2$" + ITERACIONES + "$" + encoder.encodeToString(sal) + "$" + encoder.encodeToString(hash);
    }

    public boolean coincide(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null) {
            return false;
        }
        String[] partes = hashGuardado.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(contrasena.toCharArray(), sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] derivar(char[] contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(contrasena, sal, iteraciones, LONGITUD_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No fue posible generar el hash de la contraseña", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
