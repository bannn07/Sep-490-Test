package io.fptu.sep490.utils;

import io.fptu.sep490.constant.CustomLocale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocalizedTextUtils {

    private static MessageSource messageSource;

    public LocalizedTextUtils(MessageSource messageSource) {
        LocalizedTextUtils.messageSource = messageSource;
    }

    public static String getLocalizedText(String code, Object... args) {
        return messageSource.getMessage(
                code,
                args,
                code,
                LocaleContextHolder.getLocale()
        );
    }

    public static String getLocalizedText(String code, CustomLocale locale, Object... args) {
        Locale language = switch (locale) {
            case VI -> new Locale("vi");
            case KOR -> Locale.KOREAN;
            default -> Locale.ENGLISH;
        };

        return messageSource.getMessage(
                code,
                args,
                code,
                language
        );
    }
}
