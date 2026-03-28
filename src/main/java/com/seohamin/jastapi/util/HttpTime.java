package com.seohamin.jastapi.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Http에서 사용하는 시간 포멧에 맞는 시각을 제공하는 클래스.
 */
public class HttpTime {

    private static final DateTimeFormatter HTTP_DATE_FORMATTER =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));

    // 인스턴스화 방지
    public HttpTime() {}

    /**
     * 현재 시각을 Http에서 사용하는 시간 포멧에 맞게 제공하는 메서드.
     * @return Http에서 사용하는 시간 포멧에 맞는 현재 시각
     */
    public static String getCurrentTime() {
        return HTTP_DATE_FORMATTER.format(ZonedDateTime.now());
    }
}
