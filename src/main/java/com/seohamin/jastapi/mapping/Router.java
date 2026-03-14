package com.seohamin.jastapi.mapping;

import com.seohamin.jastapi.mapping.dto.RouteDto;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<String, RouteDto> getRouterMap = new HashMap<String, RouteDto>();
    private final Map<String, RouteDto> postRouterMap = new HashMap<String, RouteDto>();
    private final Map<String, RouteDto> patchRouterMap = new HashMap<String, RouteDto>();
    private final Map<String, RouteDto> deleteRouterMap = new HashMap<String, RouteDto>();

    public void addRoute(
            final HttpMethod httpMethod,
            final String path,
            final RouteDto routeDto
    ) {

        if (httpMethod.equals(HttpMethod.GET)) {
            getRouterMap.put(path, routeDto);
        }
        else if (httpMethod.equals(HttpMethod.POST)) {
            postRouterMap.put(path, routeDto);
        }
        else if (httpMethod.equals(HttpMethod.PATCH)) {
            patchRouterMap.put(path, routeDto);
        }
        else if (httpMethod.equals(HttpMethod.DELETE)) {
            deleteRouterMap.put(path, routeDto);
        }
    }
}
