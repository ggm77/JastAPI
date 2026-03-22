package com.seohamin.jastapi.web;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.util.Converter;
import com.seohamin.jastapi.util.ErrorResponse;
import com.seohamin.jastapi.util.HttpTime;
import com.seohamin.jastapi.web.http.*;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.dto.RouteDto;

import java.lang.reflect.InvocationTargetException;

public class Dispatcher {

    // 인스턴스화 방지
    public Dispatcher() {}

    public static HttpResponse dispatch(final HttpRequest httpRequest) {
        final String method = httpRequest.getMethod();
        final String path = httpRequest.getPath();
        final String version = httpRequest.getVersion();

        final HttpMethod httpMethod;
        if ("GET".equalsIgnoreCase(method)) {
            httpMethod = HttpMethod.GET;
        }
        else if ("POST".equalsIgnoreCase(method)) {
            httpMethod = HttpMethod.POST;
        }
        else if ("PATCH".equalsIgnoreCase(method)) {
            httpMethod = HttpMethod.PATCH;
        }
        else if ("DELETE".equalsIgnoreCase(method)) {
            httpMethod = HttpMethod.DELETE;
        }
        else {
            httpMethod = null;
        }

        final RouteDto routeDto = Container.getBean(Router.class).getRoute(httpMethod, path);

        if (routeDto == null) {
            return ErrorResponse.createBadRequest(version);
        }

        final Object result;
        final byte[] body;
        try {
            result = routeDto.getMethod().invoke(routeDto.getInstance());
            body = Converter.convertToByte(result);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return ErrorResponse.createBadRequest(version);
        }

        final HttpHeader responseHeader = new HttpHeader();
        responseHeader.add("Content-Type", "application/json; charset=utf-8");
        responseHeader.add("Content-Length", String.valueOf(body.length));
        responseHeader.add("Connection", "keep-alive");
        responseHeader.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeader.add("Date", HttpTime.getCurrentTime());

        final HttpResponse httpResponse = new HttpResponse();

        httpResponse.setStatusCode(HttpStatus.OK.getStatusCode());
        httpResponse.setStatusMessage(HttpStatus.OK.getStatusMessage());
        httpResponse.setVersion(version);
        httpResponse.setBody(body);
        httpResponse.setHeader(responseHeader);

        return httpResponse;
    }

}
