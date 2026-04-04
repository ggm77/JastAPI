package com.seohamin.jastapi.web;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.util.Converter;
import com.seohamin.jastapi.util.ErrorResponse;
import com.seohamin.jastapi.util.HttpTime;
import com.seohamin.jastapi.web.http.*;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.dto.ParameterDto;
import com.seohamin.jastapi.web.mapping.dto.ParameterSource;
import com.seohamin.jastapi.web.mapping.dto.RouteDto;
import com.seohamin.jastapi.web.mapping.dto.RouteInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 외부에서 들어오는 요청을 적절히 처리하는 클래스
 */
public class Dispatcher {

    // 인스턴스화 방지
    private Dispatcher() {}

    /**
     * 클라이언트로 요청받은 Http 요청에 따라 알맞은 처리를하고 응답을 주는 메서드.
     * @param httpRequest Http 요청 정보가 담긴 객체
     * @return Http 응답 값
     */
    public static HttpResponse dispatch(final HttpRequest httpRequest) {

        if (httpRequest == null) {
            return ErrorResponse.createBadRequest("HTTP/1.1");
        }

        final String method = httpRequest.getMethod();
        final String path = httpRequest.getPath();
        final Map<String, List<String>> query = httpRequest.getQuery();
        final String version = httpRequest.getVersion();
        final byte[] requestBody = httpRequest.getBody();

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
            return ErrorResponse.createNotFound(version);
        }

        final RouteInfo routeInfo = routeDto.getRouteInfo();
        final Object result;
        final byte[] body;
        try {
            final List<ParameterDto> parameters = routeInfo.getParameters();
            final Object[] args = new Object[parameters.size()];

            for (int i = 0; i < parameters.size(); i++) {
                final ParameterDto parameterDto = parameters.get(i);

                if (parameterDto.getParameterSource().equals(ParameterSource.BODY)) {
                    args[i] = Converter.objectMapper.readValue(requestBody, parameterDto.getType());
                }
                else if (parameterDto.getParameterSource().equals(ParameterSource.PARAM)) {
                    args[i] = routeDto.getPathVariable().get(parameterDto.getAnnotationValue());
                }
                else if (parameterDto.getParameterSource().equals(ParameterSource.QUERY)) {
                    if (query == null) {
                        args[i] = Collections.emptyList();
                    } else {
                        args[i] = query.get(parameterDto.getAnnotationValue());
                    }
                }
            }

            result = routeInfo.getMethod().invoke(routeInfo.getInstance(), args);
            body = Converter.convertToByte(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ErrorResponse.createBadRequest(version);
        }

        final HttpHeader responseHeader = new HttpHeader();
        responseHeader.add("Content-Type", "application/json; charset=utf-8");
        responseHeader.add("Content-Length", String.valueOf(body.length));
//        responseHeader.add("Connection", "keep-alive");
        responseHeader.add("Connection", "close"); // while문을 통해 순차적으로 처리하기 때문
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
