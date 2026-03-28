package com.seohamin.jastapi.web.mapping.dto;

import java.lang.reflect.Method;

/**
 * 라우터에 저장될 정보의 클래스
 */
public class RouteDto {
    private Object instance;
    private Method method;

    public RouteDto(
            final Object instance,
            final Method method
    ) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }
}
