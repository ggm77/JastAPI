package com.seohamin.jastapi_example;

import com.seohamin.jastapi.JastApiApplication;

public class Main {
    public static void main(String[] args) {
        // Starting the server at 0.0.0.0:8080
        JastApiApplication.run(Main.class, false,8080);
    }
}
