package com.seohamin.jastapi.web.mapping.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 라우트를 trie의 노드로 저장하게하는 객체
 * A class representing a node within the routing Trie structure.
 */
public class RouteNode {
    // 하위 노드들
    // Child nodes for static path segments
    private Map<String, RouteNode> children = new HashMap<>();

    // 동적 세그먼트를 위한 필드
    // Field for handling dynamic path segments
    private RouteNode dynamicChild;

    // 루트 노드인지 여부
    // Flag indicating whether this node is the root of the Trie
    private boolean isRoot = false;

    // 이 노드가 최종 목적지일 때 조회할 라우트 정보
    // The route metadata to be retrieved when this node is a terminal destination
    private RouteInfo routeInfo;

    /**
     * 루트 노드를 생성하는 정적 메서드이다.
     * Static factory method to create a root node.
     * @return 루트 노드 객체 (A new root RouteNode instance.)
     */
    public static RouteNode createRootNode() {
        final RouteNode rootNode = new RouteNode();
        rootNode.setIsRoot(true);
        return rootNode;
    }

    /**
     * 하위 노드 맵에 새로운 정적 요소를 추가하는 메서드이다.
     * Adds a new static element to the map of child nodes.
     * @param key 새로운 요소의 키 (= 세그먼트) (The key for the new element (path segment).)
     * @param value 새로운 요소의 값 (= 노드) (The child node corresponding to the segment.)
     */
    public void putChildren(String key, RouteNode value) {
        children.put(key, value);
    }

    public Map<String, RouteNode> getChildren() {
        return children;
    }

    public RouteNode getDynamicChild() {
        return dynamicChild;
    }

    public boolean getIsRoot() {
        return isRoot;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public void setDynamicChild(RouteNode dynamicChild) {
        this.dynamicChild = dynamicChild;
    }

    // 루트 노드로 변경하는건 이 클래스 내부에서만 가능
    // Modification of the root status is restricted to the internal scope of this class
    private void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setRouteInfo(RouteInfo routeInfo) {
        this.routeInfo = routeInfo;
    }
}
