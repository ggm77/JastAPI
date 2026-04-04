package com.seohamin.jastapi.web.mapping.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 라우트를 trie의 노드로 저장하게하는 객체
 */
public class RouteNodeDto {
    // 하위 노드들
    private Map<String, RouteNodeDto> children = new HashMap<>();

    // 동적 세그먼트를 위한 필드
    private RouteNodeDto paramChild;

    // 루트 노드인지 여부
    private boolean isRoot = false;

    // 이 노드가 최종 목적지일 때 조회할 라우트 정보
    private RouteInfo routeInfo;

    /**
     * 루트 노드를 생성하는 정적 메서드이다.
     * @return 루트 노드 객체
     */
    public static RouteNodeDto createRootNode() {
        final RouteNodeDto rootNode = new RouteNodeDto();
        rootNode.setIsRoot(true);
        return rootNode;
    }

    /**
     * 하위 노드 맵에 새로운 정적 요소를 추가하는 메서드이다.
     * @param key 새로운 요소의 키 (= 세그먼트)
     * @param value 새로운 요소의 값 (= 노드)
     */
    public void putChildren(String key, RouteNodeDto value) {
        children.put(key, value);
    }

    public Map<String, RouteNodeDto> getChildren() {
        return children;
    }

    public RouteNodeDto getParamChild() {
        return paramChild;
    }

    public boolean getIsRoot() {
        return isRoot;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public void setParamChild(RouteNodeDto paramChild) {
        this.paramChild = paramChild;
    }

    // 루트 노드로 변경하는건 이 클래스 내부에서만 가능
    private void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setRouteInfo(RouteInfo routeInfo) {
        this.routeInfo = routeInfo;
    }
}
