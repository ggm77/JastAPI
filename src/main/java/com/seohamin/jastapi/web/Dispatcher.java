package com.seohamin.jastapi.web;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.util.Converter;
import com.seohamin.jastapi.web.http.ErrorResponse;
import com.seohamin.jastapi.web.http.*;
import com.seohamin.jastapi.web.http.exception.HttpResponseException;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.model.Parameter;
import com.seohamin.jastapi.web.mapping.model.ParameterSource;
import com.seohamin.jastapi.web.mapping.model.RouteDto;
import com.seohamin.jastapi.web.mapping.model.RouteInfo;

import java.io.IOException;
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

        // 요청 경로와 HTTP method 파싱
        final String path = httpRequest.getPath();
        final HttpMethod httpMethod = parseHttpMethod(httpRequest.getMethod());

        // 적절한 메소드 못 찾으면 502 던짐
        if (httpMethod == null) {
            return ErrorResponse.createBadGateway(httpRequest.getVersion());
        }

        // 컨테이너에서 라우터 빈에 접근해서 http method와 request path에 맞는 라우트 찾기
        final RouteDto routeDto = router.getRoute(httpMethod, path);

        // 적절한 라우트 못 찾으면 404 던짐
        if (routeDto == null) {
            return ErrorResponse.createNotFound(httpRequest.getVersion());
        }

        try {
            // HTTP 요청에서 invoke할 메서드의 매개변수 추출
            final Object[] args = resolveArguments(routeDto, httpRequest);

            // 라우트에 해당하는 메서드 찾아서 호출하고 리턴값을 적절히 가공해 응답
            return invokeAndCreateResponse(routeDto.getRouteInfo(), args, httpRequest);
        }
        // invoke 한 메서드 내부에서 일어난 예외
        catch (InvocationTargetException ex) {
            return handleInvocationException(ex, httpRequest.getVersion());
        }
        // 기타 모든 예외
        catch (Exception ex) {
            ex.printStackTrace();
            return ErrorResponse.createInternalServerError(httpRequest.getVersion());
        }
    }

    /**
     * 문자열로 들어오는 HTTP method를 Enum으로 변환하는 메서드.
     * @param methodStr 문자열로 된 HTTP method.
     * @return 적절한 HttpMethod 객체
     */
    private HttpMethod parseHttpMethod(String methodStr) {
        if ("GET".equalsIgnoreCase(methodStr)) {
            return HttpMethod.GET;
        }
        else if ("POST".equalsIgnoreCase(methodStr)) {
            return HttpMethod.POST;
        }
        else if ("PATCH".equalsIgnoreCase(methodStr)) {
            return HttpMethod.PATCH;
        }
        else if ("DELETE".equalsIgnoreCase(methodStr)) {
            return HttpMethod.DELETE;
        }
        else {
            return null;
        }
    }

    /**
     * 라우트에 저장되어있는 메서드를 호출하고, 그 리턴 값을 적절하게 처리하여 HTTP 응답을 생성하는 메서드.
     * @param routeInfo 요청에 해당하는 라우트 정보가 담긴 객체.
     * @param args invoke할 객체의 매개변수 배열
     * @param httpRequest HTTP 요청
     * @return 적절하게 생성된 HTTP 응답
     * @throws InvocationTargetException invoke한 메서드에서 예외가 터진 경우 던짐.
     * @throws IllegalAccessException invoke 할 때 매개변수가 적절하지 않다면 던짐.
     */
    private HttpResponse invokeAndCreateResponse(
            final RouteInfo routeInfo,
            final Object[] args,
            final HttpRequest httpRequest
    ) throws InvocationTargetException, IllegalAccessException {

        final String version = httpRequest.getVersion();

        // 리플랙션으로 라우터에서 찾은 메소드 실행
        final Object result = routeInfo.getMethod().invoke(routeInfo.getInstance(), args);

        // 메서드 호출 결과가 HttpResponse인 경우 캐스팅해서 바로 전송
        if (result instanceof HttpResponse) {
            return (HttpResponse) result;
        } else {
            // 바디를 byte로 변환
            final byte[] body = Converter.convertToByte(result);

            // http response 객체 필드 채우기
            final HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(HttpStatus.OK.getStatusCode());
            httpResponse.setStatusMessage(HttpStatus.OK.getStatusMessage());
            httpResponse.setVersion(version);
            httpResponse.setBody(body);
            httpResponse.setHeader(HttpHeader.getDefaultResponseHeader(body.length));

            return httpResponse;
        }
    }

    /**
     * HTTP 요청을 읽어서 invoke 할 메서드의 매개변수를 추출하는 메서드.
     * @param routeDto invoke 할 메서드 정보가 담긴 객체
     * @param httpRequest HTTP 요청 정보
     * @return 매개변수가 담긴 배열
     * @throws IOException HTTP 요청 바디를 읽기 실패한 경우 예외 던짐.
     */
    private Object[] resolveArguments(
            final RouteDto routeDto,
            final HttpRequest httpRequest
    ) throws IOException {
        final RouteInfo routeInfo = routeDto.getRouteInfo(); // 실제 객체와 메소드 정보, 파라미터 정보가 담긴 객체 추출

        final Map<String, List<String>> query = httpRequest.getQuery();
        final byte[] requestBody = httpRequest.getBody();

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

        return args;
    }

    /**
     * invoke 한 메서드 내부에서 터진 예외를 처리하는 메서드.
     * 개발자가 의도한 HttpResponseException이라면 적절한 응답을 응답하고,
     * 이외에는 500을 던진다.
     * @param ex invoke 한 메서드 내에서 터진 InvocationTargetException.
     * @param version 응답할 HTTP 버전.
     * @return 적절한 HttpResponse 또는 500.
     */
    private HttpResponse handleInvocationException(
            final InvocationTargetException ex,
            final String version
    ) {
        // 개발자가 의도적으로 일으킨 HttpResponseException이라면 적절한 응답 리턴
        final Throwable cause = ex.getCause();
        if (cause instanceof HttpResponseException) {
            return ((HttpResponseException) cause).getHttpResponse();
        } else {
            ex.printStackTrace();
            return ErrorResponse.createInternalServerError(version);
        }
    }
}
