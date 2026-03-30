package com.seohamin.jastapi_example.dto;

public class TestResponseDto {
    private String name;
    private int age;

    public TestResponseDto(
            String name,
            int age
    ) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
