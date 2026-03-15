package com.seohamin.jastapi.web.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers = new HashMap<>();

    public void add(
            final String name,
            final String value
    ) {
        headers.put(name.toLowerCase(), value);
    }

    public String getContentLength() {
        return headers.get("content-length");
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) -> {
            builder.append(key).append(": ").append(value).append("\n");
        });
        return builder.toString();
    }
}
