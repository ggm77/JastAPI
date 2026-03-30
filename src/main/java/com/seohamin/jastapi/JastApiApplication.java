package com.seohamin.jastapi;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.core.Scanner;
import com.seohamin.jastapi.web.Dispatcher;
import com.seohamin.jastapi.web.http.HttpRequest;
import com.seohamin.jastapi.web.http.HttpRequestParser;
import com.seohamin.jastapi.web.http.HttpResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * JastAPI를 동작시키는 클래스
 */
public class JastApiApplication {

    // 인스턴스화 방지
    private JastApiApplication() {}

    /**
     * JastAPI 서버를 시작하는 메서드
     * @param sourceClass 이 메서드를 호출하는 클래스
     * @param isLocalhost localhost로 열지 여부
     * @param port 서버 열 포트 번호
     */
    public static void run(
            final Class<?> sourceClass,
            final boolean isLocalhost,
            final int port
    ) {

        // check if port is valid | 포트번호 올바른지 검사
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }

        // 컨테이너 초기화로 라우터까지 자동 생성
        Container.init(Scanner.scan(sourceClass));

        try (ServerSocket serverSocket = new ServerSocket()) {

            final InetSocketAddress inetSocketAddress;

            if (isLocalhost) {
                inetSocketAddress = new InetSocketAddress("127.0.0.1", port);
            } else {
                inetSocketAddress = new InetSocketAddress("0.0.0.0", port);
            }

            serverSocket.bind(inetSocketAddress);

            // start log | 시작 로그 찍기
            System.out.println("Server started on port " + port + "...");

            // server working | 서버 작동
            while (true) {
                System.out.println("연결 대기중...");

                try (
                        final Socket socket = serverSocket.accept();
                        final InputStream in = socket.getInputStream();
                        final OutputStream out = socket.getOutputStream()
                ) {
                    System.out.println("클라이언트 연결 됨");

                    final HttpRequest httpRequest = HttpRequestParser.parse(in);

                    final HttpResponse httpResponse = Dispatcher.dispatch(httpRequest);

                    out.write(httpResponse.toBytes());
                    out.flush();
                }
            }

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
