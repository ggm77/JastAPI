package com.seohamin.jastapi_example.controller;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi.annotation.Get;
import com.seohamin.jastapi_example.dto.TestResponseDto;
import com.seohamin.jastapi_example.service.TestService;

@Component
public class TestController {

    private final TestService testService;

    public TestController (TestService testService) {
        this.testService = testService;
    }

    @Get("/hello")
    public TestResponseDto hello() {
        return testService.test();
    }

    @Get("/test")
    public String test() {
        return "test";
    }
}
