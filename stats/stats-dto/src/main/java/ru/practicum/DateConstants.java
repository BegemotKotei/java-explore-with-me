package ru.practicum;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public final class DateConstants {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

}