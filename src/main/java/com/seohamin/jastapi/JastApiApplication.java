package com.seohamin.jastapi;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.mapping.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class JastApiApplication {

    public JastApiApplication() {}

    public static void run(
            final Class<?> sourceClass,
            final boolean isLocalhost,
            final int port
    ) {

        // check if port is valid | 포트번호 올바른지 검사
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }

        // 클래스를 스캔하는 Scanner
        final Scanner scanner = new Scanner();

        // 싱글톤 객체를 저장할 컨테이너
        final Container container = new Container();

        // 스캐너로 클래스 찾고 컨테이너에 등록
        container.init(scanner.scan(sourceClass.getPackageName()));

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
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        OutputStream out = socket.getOutputStream()
                ) {
                    System.out.println("클라이언트 연결 됨");

                    final String request = in.readLine();
                    System.out.println("request: " + request);

                    final String responseBody = "<h1>Hello from my Framework!</h1>";
                    final String httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html; charset=UTF-8\r\n" +
                            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" +
                            responseBody;

                    out.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
            }

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
