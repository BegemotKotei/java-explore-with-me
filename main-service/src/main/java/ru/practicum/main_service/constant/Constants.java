package ru.practicum.main_service.constant;

import java.time.format.DateTimeFormatter;

public abstract class Constants {

    public static final int MIN_TITLE = 3;
    public static final int MAX_TITLE = 120;

    public static final int MIN_ANNOTATION = 20;
    public static final int MAX_ANNOTATION = 2000;

    public static final int MIN_DESCRIPTION = 20;
    public static final int MAX_DESCRIPTION = 7000;

    public static final String PAGE_FROM = "0";
    public static final String PAGE_SIZE = "10";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

}
