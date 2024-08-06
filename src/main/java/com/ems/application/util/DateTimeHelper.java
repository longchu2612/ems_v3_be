package com.ems.application.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DateTimeHelper {

    public static LocalDateTime convertToLocalDateTime(String stringDate) {
        if (StringUtils.hasText(stringDate)) {
            OffsetDateTime odt = OffsetDateTime.parse(stringDate);
            ZonedDateTime zdt = odt.atZoneSameInstant(ZoneId.systemDefault());
            return zdt.toLocalDateTime();
        }
        return null;
    }

    public static LocalDateTime convertToLocalDateTime(String stringDate, String pattern) {
        if (StringUtils.hasText(stringDate)) {
            if (!StringUtils.hasLength(pattern)) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(stringDate, formatter);
        }
        return null;
    }

    public static LocalDate convertDateFromString(String date, String dateFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            // convert String to LocalDate
            return LocalDate.parse(date, formatter);
        } catch (Exception exception) {
            return null;
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        if (!ObjectUtils.isEmpty(dateTime)) {
            ZoneOffset offset = OffsetDateTime.now().getOffset();
            return dateTime.atOffset(offset).toString();
        }
        return null;
    }

    public static String convertLocalDateTimeToString(LocalDateTime dateTime, String datetimeFormat) {
        if (!ObjectUtils.isEmpty(dateTime)) {
            return DateTimeFormatter.ofPattern(datetimeFormat).format(dateTime);
        }
        return null;
    }

    public static String convertLocalDateTimeToFormattedString(LocalDateTime dateTime) {
        if (!ObjectUtils.isEmpty(dateTime)) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(dateTime);
        }
        return null;
    }

    public static String convertLocalDateTimeToDateString(LocalDateTime dateTime) {
        if (!ObjectUtils.isEmpty(dateTime)) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime);
        }
        return null;
    }

    public static LocalDateTime getFirstDayOfCurrentWeek() {
        // Go backward to get Monday
        LocalDate monday = LocalDate.now();
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        return monday.atStartOfDay();
    }

    public static LocalDateTime getLastDayOfCurrentWeek() {
        // Go backward to get Monday
        LocalDate monday = LocalDate.now();
        while (monday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            monday = monday.plusDays(1);
        }
        return monday.atTime(23, 59, 59);
    }

    public static Long convertLocalDateTimeToLong(LocalDateTime dateTime) {
        if (!ObjectUtils.isEmpty(dateTime)) {
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return null;
    }
}
