package com.seohamin.jastapi.web.mapping.dto;

import java.util.Map;

/**
 * 라우트 정보와 path variable 정보를 다 같이 디스패쳐로 전달하기 위한 DTO
 */
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
