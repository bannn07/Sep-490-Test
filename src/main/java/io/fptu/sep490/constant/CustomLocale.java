package io.fptu.sep490.constant;

public enum CustomLocale {
    EN("EN"),
    KOR("KO"),
    VI("VI");

    private final String code;

    CustomLocale(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static CustomLocale fromString(String code) {
        for (CustomLocale locale : CustomLocale.values()) {
            if (locale.code.equalsIgnoreCase(code)) {
                return locale;
            }
        }
        throw new IllegalArgumentException("Unknown locale code: " + code);
    }
}