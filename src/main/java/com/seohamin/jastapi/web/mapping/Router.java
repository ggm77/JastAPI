package com.seohamin.jastapi.web.mapping;

import com.seohamin.jastapi.annotation.*;
import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.web.http.HttpMethod;
import com.seohamin.jastapi.web.mapping.dto.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component // 라우터도 컨테이너에서 빈으로서 관리됨
public class Router {

    private final Map<HttpMethod, RouteNodeDto> routerTrieMap = new HashMap<>();

    public void init(
            final Map<String, Class<?>> scannedClasses
    ) {

        // 각 Http method에 맞는 루트 생성
        for (HttpMethod httpMethod : HttpMethod.values()) {
            routerTrieMap.put(httpMethod, RouteNodeDto.createRootNode());
        }

        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);
            final Object instance = Container.getBean(clazz);

            for (final Method method : clazz.getDeclaredMethods()) {
                for (final Annotation annotation : method.getAnnotations()) {

                    final Parameter[] parameters = method.getParameters();
                    final List<ParameterDto> parameterDto = new ArrayList<>();

                    for (final Parameter param : parameters) {
                        if (param.isAnnotationPresent(RequestBody.class)) {
                            parameterDto.add(new ParameterDto(param.getName(), param.getType(), ParameterSource.BODY, null));
                        } else if (param.isAnnotationPresent(RequestParam.class)) {
                            final String annotationValue = param.getAnnotation(RequestParam.class).value();
                            parameterDto.add(new ParameterDto(param.getName(), param.getType(), ParameterSource.PARAM, annotationValue));
                        } else if (param.isAnnotationPresent(RequestQuery.class)) {
                            final String annotationValue = param.getAnnotation(RequestQuery.class).value();
                            parameterDto.add(new ParameterDto(param.getName(), param.getType(), ParameterSource.QUERY, annotationValue));
                        }
                    }

                    final RouteInfo routeInfo = new RouteInfo(instance, method, parameterDto);

                    if (annotation.annotationType().equals(Get.class)) {
                        final Get getAnnotation = (Get) annotation;
                        addRoute(HttpMethod.GET, getAnnotation.value(), routeInfo);
                    }
                    else if (annotation.annotationType().equals(Post.class)) {
                        final Post postAnnotation = (Post) annotation;
                        addRoute(HttpMethod.POST, postAnnotation.value(), routeInfo);
                    }
                    else if (annotation.annotationType().equals(Patch.class)) {
                        final Patch patchAnnotation = (Patch) annotation;
                        addRoute(HttpMethod.PATCH, patchAnnotation.value(), routeInfo);
                    }
                    else if (annotation.annotationType().equals(Delete.class)) {
                        final Delete deleteAnnotation = (Delete) annotation;
                        addRoute(HttpMethod.DELETE, deleteAnnotation.value(), routeInfo);
                    }
                }
            }
        }
    }

    public void addRoute(
            final HttpMethod httpMethod,
            final String path,
            final RouteInfo routeInfo
    ) {
        if (
                httpMethod == null
                || path == null || path.isBlank()
                || routeInfo == null
        ) {
            return ;
        }

        RouteNodeDto currentNode = routerTrieMap.get(httpMethod);
        final List<String> paramNames = new ArrayList<>();
        for (String segment : splitToSegments(path)) {

            if (segment.startsWith("{") && segment.endsWith("}")) {
                paramNames.add(segment.substring(1,segment.length()-1));

                if (currentNode.getParamChild() != null) {
                    currentNode = currentNode.getParamChild();
                } else {
                    final RouteNodeDto paramChild = new RouteNodeDto();
                    currentNode.setParamChild(paramChild);
                    currentNode = paramChild;
                }
            } else {
                final RouteNodeDto next = currentNode.getChildren().get(segment);
                if (next != null) {
                    currentNode = next;
                } else {
                    final RouteNodeDto child = new RouteNodeDto();
                    currentNode.putChildren(segment, child);
                    currentNode = child;
                }
            }
        }
        routeInfo.setParamNames(paramNames);
        currentNode.setRouteInfo(routeInfo);
    }

    public RouteDto getRoute(
            final HttpMethod httpMethod,
            final String path
    ) {
        if (httpMethod == null || path == null) {
            return null;
        }

        RouteNodeDto currentNode = routerTrieMap.get(httpMethod);
        final List<String> requestParams = new ArrayList<>();

        for (String segment : splitToSegments(path)) {
            final RouteNodeDto next = currentNode.getChildren().get(segment);

            if (next != null) {
                currentNode = next;
            } else if (currentNode.getParamChild() != null) {
                requestParams.add(segment);
                currentNode = currentNode.getParamChild();
            } else {
                return null; // 404
            }
        }
        final RouteInfo routeInfo = currentNode.getRouteInfo();
        final Map<String, String> pathVariable = new HashMap<>();
        for (int i = 0; i < requestParams.size(); i++) {
            pathVariable.put(routeInfo.getParamNames().get(i), requestParams.get(i));
        }

        return new RouteDto(routeInfo, pathVariable);
    }

    private List<String> splitToSegments(String path) {

        if (path == null || path.isBlank() || path.equals("/")) {
            return Collections.emptyList();
        }

        return Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isBlank())
                .toList();
    }
}
