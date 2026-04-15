package com.seohamin.jastapi_example.home;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.annotation.web.Get;
import com.seohamin.jastapi.web.http.ErrorResponse;
import com.seohamin.jastapi.web.http.HttpTime;
import com.seohamin.jastapi.web.http.HttpHeader;
import com.seohamin.jastapi.web.http.HttpResponse;
import com.seohamin.jastapi.web.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

@Component
public class HomeController {

    @Get("/")
    public HttpResponse getHomePage() {

        byte[] body;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("index.html")) {
            if (is != null) {
                body = is.readAllBytes();
            } else {
                body = new byte[0];
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return ErrorResponse.createNotFound("HTTP/1.1");
        }

        final HttpHeader responseHeader = new HttpHeader();
        responseHeader.add("Content-Type", "text/html; charset=utf-8");
        responseHeader.add("Content-Length", String.valueOf(body.length));
        responseHeader.add("Connection", "keep-alive");
        responseHeader.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeader.add("Date", HttpTime.getCurrentTime());


        // http response 객체 필드 채우기
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatus.OK.getStatusCode());
        httpResponse.setStatusMessage(HttpStatus.OK.getStatusMessage());
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setBody(body);
        httpResponse.setHeader(responseHeader);

        return httpResponse;
    }
}
