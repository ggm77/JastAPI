package com.seohamin.jastapi.web.mapping.dto;

import java.util.Map;

public class RouteDto {
    private final RouteInfo routeInfo;
    private final Map<String, String> pathVariable;

    public RouteDto(
            RouteInfo routeInfo,
            Map<String, String> pathVariable
    ) {
        this.routeInfo = routeInfo;
        this.pathVariable = pathVariable;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public Map<String, String> getPathVariable() {
        return pathVariable;
    }
}
