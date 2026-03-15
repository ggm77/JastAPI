package com.seohamin.jastapi.util;

import com.seohamin.jastapi.web.http.HttpHeader;
import com.seohamin.jastapi.web.http.HttpResponse;
import com.seohamin.jastapi.web.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class ErrorResponse {

    public static HttpResponse createBadRequest(
            final String version
    ) {
        final String bodyStr =
                "{\"status\":400,"
                +"\"message\":\"Bad Request\","
                +"\"timestamp\":\""+LocalDateTime.now()+"\"}";

        final byte[] body = bodyStr.getBytes(StandardCharsets.UTF_8);

        final HttpHeader header = new HttpHeader();
        header.add("Content-Type", "application/json");
        header.add("Date", HttpTime.getCurrentTime());
        header.add("Connection", "keep-alive");
        header.add("Content-Length", String.valueOf(body.length));
        header.add("Cache-Control", "no-cache, no-store");

        return new HttpResponse(
                version,
                HttpStatus.BAD_REQUEST.getStatusCode(),
                HttpStatus.BAD_REQUEST.getStatusMessage(),
                header,
                body
        );
    }
}
