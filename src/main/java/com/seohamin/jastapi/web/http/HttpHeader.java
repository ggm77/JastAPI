package com.seohamin.jastapi.web.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http의 헤더 정보에 대한 클래스
 * A class representing HTTP header information.
 */
public class HttpHeader {

    // 헤더 정보 담을 맵
    // Map to store header information
    private final Map<String, List<String>> headers = new HashMap<>();

    /**
     * 기본 응답 헤더를 만들어주는 메서드.
     * Creates a default set of HTTP response headers.
     * @param contentLength body의 길이. (The length of the response body.)
     * @return 특정 길이의 content-length를 가지고, content-type이 application/json인 HTTP 헤더.
     * (An HttpHeader object containing default headers such as
     * Content-Type (application/json), Content-Length, and Cache-Control.)
     */
    public static HttpHeader getDefaultResponseHeader(int contentLength) {
        final HttpHeader responseHeader = new HttpHeader();
        responseHeader.add("Content-Type", "application/json; charset=utf-8");
        responseHeader.add("Content-Length", String.valueOf(contentLength));
        responseHeader.add("Connection", "keep-alive");
        responseHeader.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeader.add("Date", HttpTime.getCurrentTime());

        return responseHeader;
    }

    /**
     * 헤더 맵에 해더 정보를 추가하는 메서드.
     * 헤더 key는 강제로 소문자로 변환한다.
     * Adds a header entry to the header map.
     * The header key is automatically converted to lowercase to ensure case-insensitivity.
     * @param name 헤더 key (The header key.)
     * @param value 헤더 값 (The header value.)
     */
    public void add(
            final String name,
            final String value
    ) {
        headers.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(value);
    }

    /**
     * 헤더 맵에 대한 게터
     * Getter for the header map.
     * @return 헤더 맵 (The map containing all header information.)
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * 헤더 맵에서 content-length 헤더의 값을 가져오는 메서드.
     * Retrieves the value of the Content-Length header.
     * @return content-length 헤더의 값 (The value of the Content-Length header, or null if not present.)
     */
    public String getContentLength() {
        final List<String> values = headers.get("content-length");
        if (values != null && !values.isEmpty()) {
            return values.getFirst();
        } else {
            return null;
        }
    }

    /**
     * 헤더 전체를 문자열로 변환하는 메서드.
     * Converts all headers into a formatted string separated by newlines.
     * @return 문자열로 변환된 헤더 (A string representation of the headers.)
     */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ");

            if (value != null) {
                builder.append(String.join(", ", value));
            }

            builder.append("\n");
        });
        return builder.toString();
    }

    /**
     * 헤더 전체를 CRLF 문자열로 변환하는 메서드.
     * Converts all headers into a string formatted with CRLF.
     * @return 문자열로 변환된 헤더 (A CRLF-formatted string of the headers.)
     */
    public String toCrlfString() {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ");

            if (value != null) {
                builder.append(String.join(", ", value));
            }

            builder.append("\r\n");
        });
        return builder.toString();
    }
}
