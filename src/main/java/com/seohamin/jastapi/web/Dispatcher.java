package com.seohamin.jastapi.web;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.util.Converter;
import com.seohamin.jastapi.util.ErrorResponse;
import com.seohamin.jastapi.util.HttpTime;
import com.seohamin.jastapi.web.http.*;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.dto.RouteDto;

import java.lang.reflect.InvocationTargetException;

/**
 * 외부에서 들어오는 요청을 적절히 처리하는 클래스
 */
public class Dispatcher {

    // 인스턴스화 방지
    public Dispatcher() {}

    /**
     * 클라이언트로 요청받은 Http 요청에 따라 알맞은 처리를하고 응답을 주는 메서드.
     * @param httpRequest Http 요청 정보가 담긴 객체
     * @return Http 응답 값
     */
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
