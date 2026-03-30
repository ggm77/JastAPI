package com.seohamin.jastapi_example.service;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi_example.dto.TestResponseDto;

@Component
public class TestService {

    public TestResponseDto test() {
        return new TestResponseDto(
                "Test Name",
                23
        );
    }
}
