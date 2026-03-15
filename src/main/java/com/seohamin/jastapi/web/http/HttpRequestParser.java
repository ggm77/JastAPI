package com.seohamin.jastapi.web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {

    public static HttpRequest parse(final InputStream in) throws IOException {
        final HttpRequest httpRequest = new HttpRequest();

        final String[] requestLine = readLine(in).split(" ");
        httpRequest.setMethod(requestLine[0]);
        httpRequest.setPath(requestLine[1]);
        httpRequest.setVersion(requestLine[2]);


        final HttpHeader httpHeader = new HttpHeader();

        String line;
        while (true) {
            line = readLine(in);

            if (line == null || line.isEmpty()) {
                break;
            }

            final int colonIdx = line.indexOf(':');
            if (colonIdx != -1) {
                final String key = line.substring(0, colonIdx);
                final String value = line.substring(colonIdx + 1);

                httpHeader.add(key, value);
            }
        }

        httpRequest.setHeader(httpHeader);

        final String contentLengthStr = httpHeader.getContentLength();
        if (contentLengthStr != null) {
            final int contentLength = Integer.parseInt(contentLengthStr);
            final byte[] body = new byte[contentLength];

            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = in.read(body, totalRead, contentLength - totalRead);
                totalRead += read;
            }

            httpRequest.setBody(body);
        }

        return httpRequest;
    }

    /**
     * InputStream으로 들어오는 값들을 한줄까지만 읽어서 String으로 리턴하는 헬퍼.
     * @param in InputStream
     * @return 읽어온 문자열
     * @throws IOException 입력값 읽을 때 문제 발생시 예외 터짐
     */
    private static String readLine(final InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                bos.write(b);
            }
        }

        if (b == -1 && bos.size() == 0) {
            return null;
        }

        return bos.toString(StandardCharsets.UTF_8);
    }
}
