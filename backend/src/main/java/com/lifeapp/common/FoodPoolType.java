package com.lifeapp.common;

import java.util.Locale;

public final class FoodPoolType {

    public static final String PUBLIC = "PUBLIC";
    public static final String PRIVATE = "PRIVATE";

    private FoodPoolType() {
    }

    public static String normalize(String value) {
        return normalize(value, PRIVATE);
    }

    public static String normalize(String value, String defaultValue) {
        String fallback = coerceStoredValue(defaultValue);
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (PUBLIC.equals(normalized) || PRIVATE.equals(normalized)) {
            return normalized;
        }
        throw new BadRequestException("美食池类型不支持");
    }

    public static String coerceStoredValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PRIVATE;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return PUBLIC.equals(normalized) ? PUBLIC : PRIVATE;
    }
}
