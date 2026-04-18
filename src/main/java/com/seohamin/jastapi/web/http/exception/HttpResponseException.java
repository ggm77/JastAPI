package com.seohamin.jastapi.web.http.exception;

import com.seohamin.jastapi.web.http.HttpResponse;

/**
 * An exception that allows developers to return an HTTP response immediately
 * from the business logic by throwing an exception.
 */
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
