package com.ems.config;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MsgTranslator {

    private static ResourceBundleMessageSource messageSource;

    MsgTranslator(ResourceBundleMessageSource messageSource) {
        MsgTranslator.messageSource = messageSource;
    }

    public static String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }

    public static String toLocaleSendApp(String msgCode, String localeString) {
        Locale locale = (localeString == null || localeString == "")
                ? LocaleContextHolder.getLocale()
                : new Locale(localeString);
        return messageSource.getMessage(msgCode, null, locale);
    }
}
