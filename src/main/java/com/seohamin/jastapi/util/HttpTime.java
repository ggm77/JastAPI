package com.seohamin.jastapi.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTime {

    private static final DateTimeFormatter HTTP_DATE_FORMATTER =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));

    // 인스턴스화 방지
    public HttpTime() {}

    public static String getCurrentTime() {
        return HTTP_DATE_FORMATTER.format(ZonedDateTime.now());
    }
}
