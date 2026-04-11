package com.seohamin.jastapi.web.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http의 헤더 정보에 대한 클래스
 */
public class HttpHeader {

    // 헤더 정보 담을 맵
    private final Map<String, List<String>> headers = new HashMap<>();

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
        headers.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(value);
    }

    /**
     * 헤더 맵에 대한 게터
     * @return 헤더 맵
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * 헤더 맵에서 content-length 헤더의 값을 가져오는 메서드.
     * @return content-length 헤더의 값
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
     * @return 문자열로 변환된 헤더
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
     * @return 문자열로 변환된 헤더
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
