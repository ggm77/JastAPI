package com.seohamin.jastapi.web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 입력 받는 값들을 HttpRequest 객체로 파싱하는 클래스
 */
public class HttpRequestParser {

    // 인스턴스화 방지
    private HttpRequestParser() {}

    /**
     * Http를 통해 들어오는 입력들을 받아서 적절하게 HttpRequest 객체에 넣는 메서드.
     * @param in Http로 들어오는 입력 스트림
     * @return HttpRequest 객체
     * @throws IOException 입력 값 받는 도중 에러 발생시 IOException 발생
     */
    public static HttpRequest parse(final InputStream in) throws IOException {
        final HttpRequest httpRequest = new HttpRequest();

        final String rawLine = readLine(in);

        if (rawLine == null || rawLine.trim().isBlank()) {
            return null;
        }

        final String[] requestLine = rawLine.split(" ");
        final String rawPath = requestLine[1];
        final int queryStringIndex = rawPath.indexOf('?');

        if (queryStringIndex != -1) {
            httpRequest.setPath(rawPath.substring(0, queryStringIndex));
            httpRequest.setQuery(parseQuery(rawPath.substring(queryStringIndex+1)));
        } else {
            httpRequest.setPath(rawPath);
        }

        httpRequest.setMethod(requestLine[0]);
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
            final int contentLength = Integer.parseInt(contentLengthStr.trim());
            final byte[] body = new byte[contentLength];

            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = in.read(body, totalRead, contentLength - totalRead);

                if (read == -1) {
                    return null;
                }

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

    private static Map<String, List<String>> parseQuery(final String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return Collections.emptyMap();
        }

        final Map<String, List<String>> query = new HashMap<>();
        final String[] pairs = rawQuery.split("&");

        for (String pair : pairs) {
            final int index = pair.indexOf('=');
            final String key;
            final String value;
            if (index > 0) {
                key = URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
                value = URLDecoder.decode(pair.substring(index+1), StandardCharsets.UTF_8);
            } else {
                key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                value = "";
            }

            query.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return query;
    }
}
