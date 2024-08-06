package com.ems.application.util;

import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageTranslator {

    private static final ResourceBundleMessageSource messageSource;

    static {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    public static String toLocale(String msgCode) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(msgCode, null, locale);
        } catch (Exception e) {
            return msgCode;
        }
    }

    public static String toLocale(String msgCode, String localeString) {
        Locale locale = new Locale(localeString);
        return messageSource.getMessage(msgCode, null, locale);
    }

    public static String toLocaleSendApp(String msgCode, String localeString) {
        Locale locale = (localeString == null || localeString.equals(""))
                ? LocaleContextHolder.getLocale()
                : new Locale(localeString);
        return messageSource.getMessage(msgCode, null, locale);
    }
}
