package com.seohamin.jastapi_example.home;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi.annotation.Get;
import com.seohamin.jastapi.util.ErrorResponse;
import com.seohamin.jastapi.util.HttpTime;
import com.seohamin.jastapi.web.http.HttpHeader;
import com.seohamin.jastapi.web.http.HttpResponse;
import com.seohamin.jastapi.web.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

/**
 * Controller that serves index.html.
 */
@Component
public class HomeController {

    /**
     * Serves the main page with index.html file.
     * @return HttpResponse what the body is index.html file.
     */
    @Get("/")
    public HttpResponse getHomePage() {

        // get index.html file by class loader
        byte[] body;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("index.html")) {
            if (is != null) {
                body = is.readAllBytes();
            } else {
                body = new byte[0];
            }
        }
        // if there is some IO problems, it will return 400.
        catch (IOException ex) {
            ex.printStackTrace();
            return ErrorResponse.createNotFound("HTTP/1.1");
        }

        // Build HttpResponse's http header.
        final HttpHeader responseHeader = new HttpHeader();
        responseHeader.add("Content-Type", "text/html; charset=utf-8");
        responseHeader.add("Content-Length", String.valueOf(body.length));
        responseHeader.add("Connection", "keep-alive");
        responseHeader.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeader.add("Date", HttpTime.getCurrentTime());

        // Build HttpResponse with body and http header.
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(HttpStatus.OK.getStatusCode());
        httpResponse.setStatusMessage(HttpStatus.OK.getStatusMessage());
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setBody(body);
        httpResponse.setHeader(responseHeader);

        // return HttpResponse directly.
        return httpResponse;
    }
}
