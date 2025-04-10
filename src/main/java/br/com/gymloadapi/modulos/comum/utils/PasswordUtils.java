package br.com.gymloadapi.modulos.comum.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@UtilityClass
public class PasswordUtils {

    public static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean isPasswordEquals(String newPassword, String oldPasswordEncrypted) {
        return new BCryptPasswordEncoder().matches(newPassword, oldPasswordEncrypted);
    }
}
