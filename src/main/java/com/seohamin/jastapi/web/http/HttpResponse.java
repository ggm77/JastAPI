package com.seohamin.jastapi.web.http;

public class HttpResponse {
    private String version;
    private int statusCode;
    private String statusMessage;
    private HttpHeader header;
    private byte[] body;

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
}
