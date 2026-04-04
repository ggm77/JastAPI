package com.seohamin.jastapi.web.mapping.dto;

import java.util.HashMap;
import java.util.Map;

public class RouteNodeDto {
    private Map<String, RouteNodeDto> children = new HashMap<>();
    private RouteNodeDto paramChild;
    private boolean isRoot = false;
    private RouteInfo routeInfo;

    public static RouteNodeDto createRootNode() {
        final RouteNodeDto rootNode = new RouteNodeDto();
        rootNode.setIsRoot(true);
        return rootNode;
    }

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

    private void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setRouteInfo(RouteInfo routeInfo) {
        this.routeInfo = routeInfo;
    }
}
