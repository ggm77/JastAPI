package com.seohamin.jastapi.web.mapping.model;

import java.util.Map;

/**
 * 라우트 정보와 path variable 정보를 다 같이 디스패쳐로 전달하기 위한 DTO
 * A DTO to send both route information and path variable information to the dispatcher.
 */
public class RouteDto {
    private final RouteInfo routeInfo;
    private final Map<String, String> pathVariables;

    public RouteDto(
            RouteInfo routeInfo,
            Map<String, String> pathVariables
    ) {
        this.routeInfo = routeInfo;
        this.pathVariables = pathVariables;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }
}
