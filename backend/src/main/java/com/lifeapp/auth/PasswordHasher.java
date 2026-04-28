package com.lifeapp.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHasher {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null || rawPassword == null) {
            return false;
        }
        if (isHashed(storedPassword)) {
            return ENCODER.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    public static boolean needsUpgrade(String storedPassword) {
        return storedPassword != null && !isHashed(storedPassword);
    }

    public static boolean isHashed(String storedPassword) {
        return storedPassword != null && storedPassword.startsWith("$2");
    }
}
