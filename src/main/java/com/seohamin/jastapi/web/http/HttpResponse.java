package com.seohamin.jastapi.web.http;

import java.nio.charset.StandardCharsets;

/**
 * Http 응답에 대한 정보를 저장할 클래스
 * A class that encapsulates the information for an HTTP response.
 */
public class HttpResponse {
    private String version;
    private int statusCode;
    private String statusMessage;
    private HttpHeader header;
    private byte[] body;

    // Default constructor
    public HttpResponse() {
        this.version = null;
        this.statusCode = 0;
        this.statusMessage = "";
        this.header = null;
        this.body = null;
    }

    public HttpResponse(
            final String version,
            final int statusCode,
            final String statusMessage
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.header = null;
        this.body = null;
    }

    public HttpResponse(
            final String version,
            final int statusCode,
            final String statusMessage,
            final HttpHeader header
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.header = header;
        this.body = null;
    }

    public HttpResponse(
            final String version,
            final int statusCode,
            final String statusMessage,
            final HttpHeader header,
            final byte[] body
    ) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.header = header;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setHeader(final HttpHeader header) {
        this.header = header;
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }

    /**
     * HttpResponse를 바이트로 변환하는 메서드.
     * @return byte 배열로 된 HttpResponse
     */
    public byte[] toBytes() {
        final StringBuilder sb = new StringBuilder();

        sb.append(version).append(" ")
                .append(statusCode).append(" ")
                .append(statusMessage).append("\r\n");

        if (header != null) {
            sb.append(header.toCrlfString());
        }

        sb.append("\r\n");

        final byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (body == null || body.length == 0) {
            return headerBytes;
        }

        final byte[] fullResponse = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, fullResponse, 0, headerBytes.length);
        System.arraycopy(body, 0, fullResponse, headerBytes.length, body.length);

        return fullResponse;
    }
}
