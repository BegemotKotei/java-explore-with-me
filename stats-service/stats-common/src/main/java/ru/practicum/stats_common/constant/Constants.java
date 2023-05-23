package ru.practicum.stats_common.constant;

import java.time.format.DateTimeFormatter;

public abstract class Constants {
    public static final String HIT_ENDPOINT = "/hit";
    public static final String STATS_ENDPOINT = "/stats";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
}
