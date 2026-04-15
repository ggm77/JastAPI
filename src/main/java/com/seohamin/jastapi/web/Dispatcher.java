package com.seohamin.jastapi.web;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.util.Converter;
import com.seohamin.jastapi.web.http.ErrorResponse;
import com.seohamin.jastapi.web.http.HttpTime;
import com.seohamin.jastapi.web.http.*;
import com.seohamin.jastapi.web.http.exception.HttpResponseException;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.model.Parameter;
import com.seohamin.jastapi.web.mapping.model.ParameterSource;
import com.seohamin.jastapi.web.mapping.model.RouteDto;
import com.seohamin.jastapi.web.mapping.model.RouteInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 외부에서 들어오는 요청을 적절히 처리하는 클래스
 */
@Component
public class Dispatcher {

    private final Router router;

    public Dispatcher(Router router) {
        this.router = router;
    }

    /**
     * 클라이언트로 요청받은 Http 요청에 따라 알맞은 처리를하고 응답을 주는 메서드.
     * @param httpRequest Http 요청 정보가 담긴 객체
     * @return Http 응답 값
     */
    public HttpResponse dispatch(final HttpRequest httpRequest) {

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

        // 컨테이너에서 라우터 빈에 접근해서 http method와 request path에 맞는 라우트 찾기
        final RouteDto routeDto = router.getRoute(httpMethod, path);

        // 못 찾으면 404 던짐
        if (routeDto == null) {
            return ErrorResponse.createNotFound(version);
        }

        final RouteInfo routeInfo = routeDto.getRouteInfo(); // 실제 객체와 메소드 정보, 파라미터 정보가 담긴 객체 추출
        final Object result;
        final byte[] body;
        final boolean isHttpResponse;
        try {
            final List<Parameter> parameters = routeInfo.getParameters();
            final Object[] args = new Object[parameters.size()];

            // 미리 서버 켜질 때 분석 했던 메소드의 파라미터 정보에 맞게 파라미터 값 찾기
            for (int i = 0; i < parameters.size(); i++) {
                final Parameter parameter = parameters.get(i);

                // 해당 파라미터가 RequestBody일 때
                if (parameter.getParameterSource().equals(ParameterSource.BODY)) {
                    // 바디 타입에 맞춰서 자동으로 타입 변환
                    args[i] = Converter.objectMapper.readValue(requestBody, parameter.getType());
                }
                // 해당 파라미터가 PathVariable일 때
                else if (parameter.getParameterSource().equals(ParameterSource.PATH)) {
                    // 무조건 String으로만 반환
                    args[i] = routeDto.getPathVariables().get(parameter.getAnnotationValue());
                }
                // 해당 파라미터가 RequestParam일 때
                else if (parameter.getParameterSource().equals(ParameterSource.PARAM)) {
                    // 쿼리에 키만 적혀있고, 값은 없는 경우
                    if (query == null) {
                        // 빈 리스트 반환
                        args[i] = Collections.emptyList();
                    }
                    // 키, 값이 모두 있는 경우
                    else {
                        // 요소가 몇개든 List<String>으로 반환
                        args[i] = query.get(parameter.getAnnotationValue());
                    }
                }
                // 해당 파라미터의 타입이 HttpRequest인 경우
                else if (parameter.getType().isAssignableFrom(HttpRequest.class)) {
                    // 요청 받은 request 그대로 전달
                    args[i] = httpRequest;
                }
            }

            // 리플랙션으로 라우터에서 찾은 메소드 실행
            result = routeInfo.getMethod().invoke(routeInfo.getInstance(), args);

            // 결과가 HttpResponse인지 여부
            isHttpResponse = result instanceof HttpResponse;

            if (!isHttpResponse) {
                // 실행 결과 byte 배열로 변환
                body = Converter.convertToByte(result);
            } else {
                body = new byte[0];
            }
        }
        // invoke한 메서드 내부에서 일어난 예외
        catch (InvocationTargetException ex) {
            final Throwable cause = ex.getCause();

            if (cause instanceof HttpResponseException) {
                return ((HttpResponseException) cause).getHttpResponse();
            } else {
                ex.printStackTrace();
                return ErrorResponse.createInternalServerError(version);
            }
        }
        // 기타 모든 예외
        catch (Exception ex) {
            ex.printStackTrace();
            return ErrorResponse.createInternalServerError(version);
        }

        // 메서드 호출 결과가 HttpResponse인 경우 캐스팅해서 바로 전송
        if (isHttpResponse) {
            return (HttpResponse) result;
        } else {
            // response에 넣을 헤더 제작
            final HttpHeader responseHeader = new HttpHeader();
            responseHeader.add("Content-Type", "application/json; charset=utf-8");
            responseHeader.add("Content-Length", String.valueOf(body.length));
            responseHeader.add("Connection", "keep-alive");
            responseHeader.add("Cache-Control", "no-cache, no-store, must-revalidate");
            responseHeader.add("Date", HttpTime.getCurrentTime());


            // http response 객체 필드 채우기
            final HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(HttpStatus.OK.getStatusCode());
            httpResponse.setStatusMessage(HttpStatus.OK.getStatusMessage());
            httpResponse.setVersion(version);
            httpResponse.setBody(body);
            httpResponse.setHeader(responseHeader);

            return httpResponse;
        }
    }

}
