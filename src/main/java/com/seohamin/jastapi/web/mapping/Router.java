package com.seohamin.jastapi.web.mapping;

import com.seohamin.jastapi.annotation.Delete;
import com.seohamin.jastapi.annotation.Get;
import com.seohamin.jastapi.annotation.Patch;
import com.seohamin.jastapi.annotation.Post;
import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.web.http.HttpMethod;
import com.seohamin.jastapi.web.mapping.dto.RouteDto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Router {

    private final Map<String, RouteDto> getRouterMap;
    private final Map<String, RouteDto> postRouterMap;
    private final Map<String, RouteDto> patchRouterMap;
    private final Map<String, RouteDto> deleteRouterMap;

    public Router() {
        getRouterMap = new HashMap<>();
        postRouterMap = new HashMap<>();
        patchRouterMap = new HashMap<>();
        deleteRouterMap = new HashMap<>();
    }

    public void init(
            final Map<String, Class<?>> scannedClasses
    ) {
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);
            final Object instance = Container.getBean(clazz);

            for (final Method method : clazz.getDeclaredMethods()) {
                for (final Annotation annotation : method.getAnnotations()) {

                    final RouteDto routeDto = new RouteDto(instance, method);

                    if (annotation.annotationType().equals(Get.class)) {
                        final Get getAnnotation = (Get) annotation;
                        addRoute(HttpMethod.GET, getAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Post.class)) {
                        final Post postAnnotation = (Post) annotation;
                        addRoute(HttpMethod.POST, postAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Patch.class)) {
                        final Patch patchAnnotation = (Patch) annotation;
                        addRoute(HttpMethod.PATCH, patchAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Delete.class)) {
                        final Delete deleteAnnotation = (Delete) annotation;
                        addRoute(HttpMethod.DELETE, deleteAnnotation.value(), routeDto);
                    }
                }
            }
        }
    }

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

    public RouteDto getRoute(
            final HttpMethod httpMethod,
            final String path
    ) {
        if (httpMethod == null || path == null) {
            return null;
        }

        if (httpMethod.equals(HttpMethod.GET)) {
            return getRouterMap.get(path);
        }
        else if (httpMethod.equals(HttpMethod.POST)) {
            return postRouterMap.get(path);
        }
        else if (httpMethod.equals(HttpMethod.PATCH)) {
            return patchRouterMap.get(path);
        }
        else if (httpMethod.equals(HttpMethod.DELETE)) {
            return deleteRouterMap.get(path);
        }

        return null;
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
