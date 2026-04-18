package com.seohamin.jastapi.web.http;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Http에서 사용하는 시간 포멧에 맞는 시각을 제공하는 클래스.
 * A utility class that provides timestamps formatted according to the HTTP protocol standards.
 */
public class HttpTime {

    private static final DateTimeFormatter HTTP_DATE_FORMATTER =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));

    // 인스턴스화 방지 | Prevents instantiation
    private HttpTime() {}

    /**
     * 현재 시각을 Http에서 사용하는 시간 포멧에 맞게 제공하는 메서드.
     * Retrieves the current time formatted as a standard HTTP-date string.
     * @return Http에서 사용하는 시간 포멧에 맞는 현재 시각 (The current time in the RFC 1123 format.)
     */
    public static String getCurrentTime() {
        return HTTP_DATE_FORMATTER.format(ZonedDateTime.now());
    }
}
