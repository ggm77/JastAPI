package com.seohamin.jastapi.web.http.exception;

import com.seohamin.jastapi.web.http.HttpResponse;

public class HttpResponseException extends RuntimeException {

    private final HttpResponse httpResponse;

    public HttpResponseException(HttpResponse httpResponse) {
        super();
        this.httpResponse = httpResponse;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
