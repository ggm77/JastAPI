package com.seohamin.jastapi;

import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.core.Scanner;
import com.seohamin.jastapi.web.Dispatcher;
import com.seohamin.jastapi.web.http.HttpRequest;
import com.seohamin.jastapi.web.http.HttpRequestParser;
import com.seohamin.jastapi.web.http.HttpResponse;
import com.seohamin.jastapi.web.mapping.Router;
import com.seohamin.jastapi.web.mapping.RouterInitializer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JastApiApplication {

    // 최대 헤더 크기 = 8KB
    private static final int MAX_HEADER_SIZE = 8192;

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

        // 라우터 생성
        final Router router = new Router();

        // 스캐너로 클래스 탐지
        final Set<Class<?>> scannedClasses = scanner.scan(sourceClass.getPackageName());

        // 스캐너로 찾은 클래스 컨테이너에 등록
        container.init(scannedClasses);

        // 스캐너로 찾은 클래스에서 어노테이션 탐지해서 라우터에 등록
        RouterInitializer.init(
                router,
                container,
                scannedClasses
        );


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

                    final HttpResponse httpResponse = Dispatcher.dispatch(httpRequest, router);
                    System.out.println(httpResponse);

                    out.write(httpResponse.toBytes());
                    out.flush();
                }
            }

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
