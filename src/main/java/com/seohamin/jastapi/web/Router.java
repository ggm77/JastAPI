package com.seohamin.jastapi.web;

import com.seohamin.jastapi.web.dto.RouteDto;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<String, RouteDto> getRouterMap = new HashMap<>();
    private final Map<String, RouteDto> postRouterMap = new HashMap<>();
    private final Map<String, RouteDto> patchRouterMap = new HashMap<>();
    private final Map<String, RouteDto> deleteRouterMap = new HashMap<>();

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

    public Map<String, RouteDto> getGetRouterMap() {
        return getRouterMap;
    }

    public Map<String, RouteDto> getPostRouterMap() {
        return postRouterMap;
    }

    public Map<String, RouteDto> getPatchRouterMap() {
        return patchRouterMap;
    }

    public Map<String, RouteDto> getDeleteRouterMap() {
        return deleteRouterMap;
    }
}
