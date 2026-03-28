package com.seohamin.jastapi.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Http의 헤더 정보에 대한 클래스
 */
public class HttpHeader {

    // 헤더 정보 담을 맵
    private final Map<String, String> headers = new HashMap<>();

    /**
     * 헤더 맵에 해더 정보를 추가하는 메서드.
     * 헤더 key는 강제로 소문자로 변환한다.
     * @param name 헤더 key
     * @param value 헤더 값
     */
    public void add(
            final String name,
            final String value
    ) {
        headers.put(name.toLowerCase(), value);
    }

    /**
     * 헤더 맵에서 content-length 헤더의 값을 가져오는 메서드.
     * @return content-length 헤더의 값
     */
    public String getContentLength() {
        return headers.get("content-length");
    }

    /**
     * 헤더 전체를 문자열로 변환하는 메서드.
     * @return 문자열로 변환된 헤더
     */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ").append(value).append("\n");
        });
        return builder.toString();
    }

    /**
     * 헤더 전체를 CRLF 문자열로 변환하는 메서드.
     * @return 문자열로 변환된 헤더
     */
    public String toCrlfString() {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ").append(value).append("\r\n");
        });
        return builder.toString();
    }
}
