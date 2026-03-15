package com.seohamin.jastapi.web.dto;

import java.lang.reflect.Method;

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
