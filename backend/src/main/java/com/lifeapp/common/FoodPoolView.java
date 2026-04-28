package com.lifeapp.common;

import java.util.Locale;

public final class FoodPoolView {

    public static final String PUBLIC_FIRST = "PUBLIC_FIRST";
    public static final String PUBLIC_ONLY = "PUBLIC_ONLY";
    public static final String PRIVATE_ONLY = "PRIVATE_ONLY";

    private FoodPoolView() {
    }

    public static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PUBLIC_FIRST;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (PUBLIC_FIRST.equals(normalized) || PUBLIC_ONLY.equals(normalized) || PRIVATE_ONLY.equals(normalized)) {
            return normalized;
        }
        throw new BadRequestException("美食池视图不支持");
    }
}
