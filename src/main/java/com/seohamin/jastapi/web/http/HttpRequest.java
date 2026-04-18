package com.seohamin.jastapi.web.http;

import java.util.List;
import java.util.Map;

/**
 * Http 요청에 대한 정보를 저장할 클래스
 * A class that encapsulates information about an incoming HTTP request.
 */
public class HttpRequest {
    private String method;
    private String path;
    private Map<String, List<String>> query;
    private String version;
    private HttpHeader header;
    private byte[] body;

    // Default constructor
    public HttpRequest() {
        this.method = null;
        this.path = null;
        this.query = null;
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
        this.query = null;
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
        this.query = null;
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
        this.query = null;
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public HttpRequest(
            final String method,
            final String path,
            final Map<String, List<String>> query,
            final String version,
            final HttpHeader header,
            final byte[] body
    ) {
        this.method = method;
        this.path = path;
        this.query = null;
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

    public Map<String, List<String>> getQuery() {
        return query;
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

    public void setMethod(final String method) {
        this.method = method;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void setQuery(final Map<String, List<String>> query) {
        this.query = query;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setHeader(final HttpHeader header) {
        this.header = header;
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }
}
