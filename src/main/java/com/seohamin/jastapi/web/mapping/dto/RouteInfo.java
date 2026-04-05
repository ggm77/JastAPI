package com.seohamin.jastapi.web.mapping.dto;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 라우터에 저장될 정보의 클래스
 */
public class RouteInfo {
    private Object instance;
    private Method method;
    private List<ParameterDto> parameters;
    private List<String> pathVariableNames;

    public RouteInfo(
            final Object instance,
            final Method method,
            final List<ParameterDto> parameters
    ) {
        this.instance = instance;
        this.method = method;
        this.parameters = parameters;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public List<ParameterDto> getParameters() {
        return parameters;
    }

    public List<String> getPathVariableNames() {
        return pathVariableNames;
    }

    public void setPathVariableNames(List<String> pathVariableNames) {
        this.pathVariableNames = pathVariableNames;
    }
}
