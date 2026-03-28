package com.seohamin.jastapi.web.http;

/**
 * Http 요청에 대한 정보를 저장할 클래스
 */
public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private HttpHeader header;
    private byte[] body;

    // Default constructor
    public HttpRequest() {
        this.method = null;
        this.path = null;
        this.version = null;
        this.header = null;
        this.body = null;
    }

    public HttpRequest(
            final String method,
            final String path,
            final String version
    ) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.header = null;
        this.body = null;
    }

    public HttpRequest(
            final String method,
            final String path,
            final String version,
            final HttpHeader header
    ) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.header = header;
        this.body = null;
    }

    public HttpRequest(
            final String method,
            final String path,
            final String version,
            final HttpHeader header,
            final byte[] body
    ) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public String setMethod(final String method) {
        return this.method = method;
    }

    public String setPath(final String path) {
        return this.path = path;
    }

    public String setVersion(final String version) {
        return this.version = version;
    }

    public HttpHeader setHeader(final HttpHeader header) {
        return this.header = header;
    }

    public byte[] setBody(final byte[] body) {
        return this.body = body;
    }
}
