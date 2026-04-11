package com.seohamin.jastapi.util;

import com.seohamin.jastapi.web.http.HttpHeader;
import com.seohamin.jastapi.web.http.HttpResponse;
import com.seohamin.jastapi.web.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 에러 발생 했을 때 사용할 HttpResponse를 생성하는 클래스.
 */
public class ErrorResponse {

    // 인스턴스화 방지
    private ErrorResponse() {}

    /**
     * BadRequest에 대한 HttpResponse를 생성한다.
     * @param version Http 버전
     * @return BadRequest에 대한 HttpResponse
     */
    public static HttpResponse createBadRequest(
            final String version
    ) {
        final String bodyStr =
                "{\"status\":400,"
                +"\"message\":\"Bad Request\","
                +"\"timestamp\":\""+LocalDateTime.now()+"\"}";

        final byte[] body = bodyStr.getBytes(StandardCharsets.UTF_8);

        return new HttpResponse(
                version,
                HttpStatus.BAD_REQUEST.getStatusCode(),
                HttpStatus.BAD_REQUEST.getStatusMessage(),
                getDefaultHeader(body.length),
                body
        );
    }

    /**
     * NotFound에 대한 HttpResponse를 생성한다.
     * @param version Http 버전
     * @return NotFound에 대한 HttpResponse
     */
    public static HttpResponse createNotFound(
            final String version
    ) {
        final String bodyStr =
                "{\"status\":404,"
                        +"\"message\":\"Not Found\","
                        +"\"timestamp\":\""+LocalDateTime.now()+"\"}";

        final byte[] body = bodyStr.getBytes(StandardCharsets.UTF_8);

        return new HttpResponse(
                version,
                HttpStatus.BAD_REQUEST.getStatusCode(),
                HttpStatus.BAD_REQUEST.getStatusMessage(),
                getDefaultHeader(body.length),
                body
        );
    }

    private static HttpHeader getDefaultHeader(int contentLength) {
        final HttpHeader header = new HttpHeader();
        header.add("Content-Type", "application/json");
        header.add("Date", HttpTime.getCurrentTime());
        header.add("Connection", "close");
        header.add("Content-Length", String.valueOf(contentLength));
        header.add("Cache-Control", "no-cache, no-store");

        return header;
    }
}
