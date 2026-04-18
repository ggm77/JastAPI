package com.seohamin.jastapi.web.http;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 에러 발생 했을 때 사용할 HttpResponse를 생성하는 클래스.
 * A factory class for creating standard HttpResponse objects
 * to be sent when an error occurs.
 */
public class ErrorResponse {

    // 인스턴스화 방지 | Prevents instantiation
    private ErrorResponse() {}

    /**
     * BadRequest에 대한 HttpResponse를 생성한다.
     * Creates an HTTP response for a 400 Bad Request error.
     * @param version Http 버전 (The HTTP version.)
     * @return BadRequest에 대한 HttpResponse (An HttpResponse representing a Bad Request.)
     */
    public static HttpResponse createBadRequest(
            final String version
    ) {
        final byte[] body = getBody(HttpStatus.BAD_REQUEST);

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
     * Creates an HTTP response for a 404 Not Found error.
     * @param version Http 버전 (The HTTP version.)
     * @return NotFound에 대한 HttpResponse (An HttpResponse representing a Not Found.)
     */
    public static HttpResponse createNotFound(
            final String version
    ) {
        final byte[] body = getBody(HttpStatus.NOT_FOUND);

        return new HttpResponse(
                version,
                HttpStatus.NOT_FOUND.getStatusCode(),
                HttpStatus.NOT_FOUND.getStatusMessage(),
                getDefaultHeader(body.length),
                body
        );
    }

    /**
     * InternalServerError에 대한 HttpResponse를 생성한다.
     * Creates an HTTP response for a 500 Internal Server Error.
     * @param version Http 버전 (The HTTP version.)
     * @return InternalServerError에 대한 HttpResponse (An HttpResponse representing a Internal Server Error.)
     */
    public static HttpResponse createInternalServerError(
            final String version
    ) {

        final byte[] body = getBody(HttpStatus.INTERNAL_SERVER_ERROR);

        return new HttpResponse(
                version,
                HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR.getStatusMessage(),
                getDefaultHeader(body.length),
                body
        );
    }

    /**
     * BadGateway에 대한 HttpResponse를 생성한다.
     * Creates an HTTP response for a 502 Bad Gateway error.
     * @param version Http 버전 (The HTTP version.)
     * @return BadGateway에 대한 HttpResponse  (An HttpResponse representing a Bad Gateway.)
     */
    public static HttpResponse createBadGateway(
            final String version
    ) {

        final byte[] body = getBody(HttpStatus.BAD_GATEWAY);

        return new HttpResponse(
                version,
                HttpStatus.BAD_GATEWAY.getStatusCode(),
                HttpStatus.BAD_GATEWAY.getStatusMessage(),
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

    private static byte[] getBody(HttpStatus httpStatus) {
        return ("{\"status\":"+httpStatus.getStatusCode()+","
                        +"\"message\":\""+httpStatus.getStatusMessage()+"\","
                        +"\"timestamp\":\""+LocalDateTime.now()+"\"}").getBytes(StandardCharsets.UTF_8);
    }
}
