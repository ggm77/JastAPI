package com.seohamin.jastapi_example.controller;

import com.seohamin.jastapi.annotation.Get;

public class TestController {
    @Get("/hello")
    public String hello() {
        return "Hello World";
    }
}
