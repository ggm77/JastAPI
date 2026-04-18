package com.seohamin.jastapi.web.mapping;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.annotation.web.*;
import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.web.http.HttpMethod;
import com.seohamin.jastapi.web.mapping.model.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 요청받은 주소를 기반으로 해당하는 기능을 제공하는 메서드를 찾아주는 라우터.
 * 라우터도 컨테이너에서 빈으로 관리됨.
 * A router that resolves the appropriate handler method based on the requested URI.
 * The Router is also managed as a bean within the application container.
 */
@Component
public class Router {

    private final Container container;

    public Router(Container container) {
        this.container = container;
    }

    // Http method 별 라우팅 trie의 루트 노드를 저장할 맵
    // A map storing the root nodes of the routing Trie for each HTTP method
    private final Map<HttpMethod, RouteNode> routerTrieMap = new HashMap<>();

    /**
     * 라우터를 초기화 시키는 메서드.
     * 스캔된 클래스들을 기반으로 컨테이너에서 빈을 꺼내온 뒤
     * 객체, 메서드, 어노테이션을 체크해서 적절한 라우트를 생성하고 저장함.
     * 라우팅 trie의 루트 노드가 여기서 생성됨.
     * Initializes the router.
     * It retrieves beans from the container based on scanned classes,
     * inspects methods and annotations to create routes, and constructs the routing Tries.
     * @param scannedClasses Scanner를 통해 스캔된 클래스의 맵 (A map of classes identified during the scanning process.)
     */
    public void init(
            final Map<String, Class<?>> scannedClasses
    ) {

        // 각 Http method에 맞는 루트 생성
        // Initialize root nodes for each HTTP method
        for (HttpMethod httpMethod : HttpMethod.values()) {
            routerTrieMap.put(httpMethod, RouteNode.createRootNode());
        }

        // 스캔 된 모든 클래스에 대해서 반복
        // Iterate through all scanned classes
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            // 컨테이너에서 해당 클래스의 빈(인스턴스) 가져오기
            // Retrieve the bean instance from the container
            final Object instance = container.getBean(clazz);

            // 해당 클래스에 존재하는 모든 메서드에 대해서 반복
            // Iterate through all methods in the class
            for (final Method method : clazz.getDeclaredMethods()) {
                // 해당 메서드에 존재하는 모든 어노테이션에 대해서 반복
                // Iterate through all annotations on the method
                for (final Annotation annotation : method.getAnnotations()) {

                    // 메서드가 필요로 하는 모든 파라미터 가져오기
                    // Get all parameters required by the method
                    final java.lang.reflect.Parameter[] parameters = method.getParameters();

                    // 파라미터 정보 저장할 객체의 리스트 생성
                    // Create a list to store parameter metadata
                    final List<Parameter> parameterDtos = new ArrayList<>();


                    // 메서드 파라미터에 붙은 어노테이션을 분석하여 매핑 정보 생성
                    // Analyze parameter annotations to create mapping information
                    for (final java.lang.reflect.Parameter param : parameters) {
                        // 어노테이션에 따라 적절한 정보 담아서 저장
                        if (param.isAnnotationPresent(RequestBody.class)) {
                            parameterDtos.add(new Parameter(param.getName(), param.getType(), ParameterSource.BODY, null));
                        } else if (param.isAnnotationPresent(PathVariable.class)) {
                            final String annotationValue = param.getAnnotation(PathVariable.class).value();
                            parameterDtos.add(new Parameter(param.getName(), param.getType(), ParameterSource.PATH, annotationValue));
                        } else if (param.isAnnotationPresent(RequestParam.class)) {
                            final String annotationValue = param.getAnnotation(RequestParam.class).value();
                            parameterDtos.add(new Parameter(param.getName(), param.getType(), ParameterSource.PARAM, annotationValue));
                        } else {
                            parameterDtos.add(new Parameter(param.getName(), param.getType(), ParameterSource.DEFAULT, null));
                        }
                    }

                    // 라우팅 trie에 저장할 객체 생성
                    // Create RouteInfo object to be stored in the routing Trie
                    final RouteInfo routeInfo = new RouteInfo(instance, method, parameterDtos);

                    // Http method에 따라 적절한 trie에 저장
                    // Register the route in the corresponding Trie based on the HTTP method annotation
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

    /**
     * 생성된 라우트 정보를 적절한 라우팅 trie에 추가하는 메서드.
     * 주로 라우터 초기화 할 때 사용함.
     * Adds route information to the corresponding routing Trie.
     * Primarily used during router initialization.
     * @param httpMethod 해당 라우트의 http method (The HTTP method of the route.)
     * @param path 해당 라우트의 엔드포인트 (The endpoint path of the route.)
     * @param routeInfo 라우팅 trie에 저장할 정보가 담긴 객체 (Metadata about the route to be stored.)
     */
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

        // 루트 노드부터 trie 순회
        // Traverse the Trie starting from the root node
        RouteNode currentNode = routerTrieMap.get(httpMethod);

        // path variable의 이름을 저장하는 리스트 (예: {id}에서 "id")
        // 나중에 라우터에서 조회 할 때 사용하므로 요소의 순서가 매우 중요
        // List to store path variable names (e.g., "id" from "{id}")
        // Order is critical for later retrieval
        final List<String> pathVariableNames = new ArrayList<>();

        // path를 '/'단위로 잘라서 trie 순회
        // Split the path by '/' and traverse the Trie
        for (String segment : splitToSegments(path)) {

            // path variable인 경우
            // Handle dynamic segments (path variables)
            if (segment.startsWith("{") && segment.endsWith("}")) {

                // 중괄호 삭제한 순수한 이름만 리스트에 추가
                // Add the pure name (without braces) to the list
                pathVariableNames.add(segment.substring(1,segment.length()-1));

                // 현재 노드에 이미 생성 된 동적 세그먼트(= path variable이 들어갈 자리)가 존재하는 경우
                // If a dynamic child already exists, select it; otherwise, create a new one
                if (currentNode.getDynamicChild() != null) {
                    // 해당 동적 세그먼트를 다음 노드로 선택
                    currentNode = currentNode.getDynamicChild();
                } else {
                    // 새로운 노드를 만들고 그걸 현재 노드의 동적 세그먼트 자리에 넣기
                    final RouteNode dynamicChild = new RouteNode();
                    currentNode.setDynamicChild(dynamicChild);

                    // 생성한 노드를 다음 노드로 선택
                    currentNode = dynamicChild;
                }
            }
            // path variable이 아닌 정적 세그먼트인 경우
            // Handle static segments
            else {

                // 현재 노드에 다음 노드가 이미 저장되어 있는지 확인
                final RouteNode next = currentNode.getChildren().get(segment);

                // 저장 되어있다면
                if (next != null) {
                    // 해당 노드를 다음 노드로 선택
                    currentNode = next;
                }
                // 저장 되어있지 않다면
                else {
                    // 새로운 노드 생성하고 그 노드를 children에 추가
                    final RouteNode child = new RouteNode();
                    currentNode.putChildren(segment, child);

                    // 추가한 노드를 다음 노드로 선택
                    currentNode = child;
                }
            }
        }

        // path variable의 이름들을 라우트 정보에 추가
        // Add path variable names to the route metadata
        routeInfo.setPathVariableNames(pathVariableNames);

        // 현재 노드(= 최종 도착한 목적지)에 라우트 정보를 저장
        // Store the route information in the leaf node (final destination)
        currentNode.setRouteInfo(routeInfo);
    }

    /**
     * 라우터에서 라우트를 조회하는 메서드.
     * 주로 디스패쳐에서 사용됨.
     * Retrieves a route from the router.
     * Primarily used by the Dispatcher to handle incoming requests.
     * @param httpMethod 조회할 라우트의 http method (The HTTP method to look up.)
     * @param path 조회할 경로 (The request path to look up.)
     * @return 조회 된 라우트의 정보가 담긴 DTO (A RouteDto containing the resolved route and path variables.)
     */
    public RouteDto getRoute(
            final HttpMethod httpMethod,
            final String path
    ) {
        if (httpMethod == null || path == null) {
            return null;
        }

        // 루트 노드부터 trie 순회 | Traverse the Trie starting from the root node
        RouteNode currentNode = routerTrieMap.get(httpMethod);

        // path variable 자리에 적힌 실제 값을 저장할 리스트
        // List to store actual values provided in path variable positions
        final List<String> pathVariableValues = new ArrayList<>();

        // path를 '/' 기준으로 잘라 trie 순회
        // Split the path by '/' and traverse the Trie
        for (String segment : splitToSegments(path)) {

            // 세그먼트로 다음 노드가 정적 세그먼트 중에 존재하는지 조회
            // Check for a matching static segment
            final RouteNode next = currentNode.getChildren().get(segment);

            // 존재한다면
            if (next != null) {
                // 조회한 세그먼트를 다음 노드로 선택
                currentNode = next;
            }
            // 존재하지 않지만 동적 세그먼트는 존재하는 경우
            // If no static match, check for a dynamic segment
            else if (currentNode.getDynamicChild() != null) {
                // path variable 자리에 적힌 실제 값을 리스트에 추가
                pathVariableValues.add(segment);

                // 해당 노드를 다음 노드로 선택
                currentNode = currentNode.getDynamicChild();
            }
            // 아무 노드도 못 찾은 경우 null 반환해서 디스패쳐에서 404 던지게 하기
            // If no match is found, return null (triggering a 404 in Dispatcher)
            else {
                return null;
            }
        }

        // 현재 노드(= 도착한 최종 목적지)에서 라우트 정보 추출
        // Extract route metadata from the final node
        final RouteInfo routeInfo = currentNode.getRouteInfo();

        // path variable 이름들을 키로, 실제 요청한 값을 value로 하는 맵 생성
        // Map path variable names to their corresponding values from the request
        final Map<String, String> pathVariables = new HashMap<>();
        for (int i = 0; i < pathVariableValues.size(); i++) {
            pathVariables.put(routeInfo.getPathVariableNames().get(i), pathVariableValues.get(i));
        }

        // DTO로 만들어서 리턴
        return new RouteDto(routeInfo, pathVariables);
    }

    /**
     * 경로를 받아서 tire 순회 할 수 있도록 '/'를 기준으로 문자열을 자르는 메서드.
     * Splits the path into segments based on '/' for Trie traversal.
     * @param path 자를 문자열 (The path string to split.)
     * @return List에 들어간 잘린 문자열 (A list of path segments.)
     */
    private List<String> splitToSegments(String path) {

        if (path == null || path.isBlank() || path.equals("/")) {
            return Collections.emptyList();
        }

        return Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isBlank())
                .toList();
    }
}
