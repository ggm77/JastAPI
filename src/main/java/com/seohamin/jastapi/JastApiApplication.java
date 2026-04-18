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
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JastAPI를 동작시키는 클래스
 * Starting point of JastAPI
 */
public class JastApiApplication {

    // 인스턴스화 방지 | prevent generate a instance
    private JastApiApplication() {}

    /**
     * JastAPI 서버를 시작하는 외부에서 호출하는 메서드
     * Method for externally starting the JastAPI server
     * @param sourceClass 이 메서드를 호출하는 클래스 (sourceClass The class that invokes this method.)
     * @param isLocalhost localhost로 열지 여부 (isLocalhost Whether to bind the server to localhost.)
     * @param port 서버 열 포트 번호 (port The port number on which the server will be opened.)
     */
    public static void run(
            final Class<?> sourceClass,
            final boolean isLocalhost,
            final int port
    ) {
        new JastApiApplication().server(sourceClass, isLocalhost, port);
    }

    /**
     * 실제 서버를 시작 시키는 메서드
     * Starts the actual server.
     * @param sourceClass 이 메서드를 호출하는 클래스 (The class that invokes this method.)
     * @param isLocalhost localhost로 열지  (Whether to host the server on localhost.)
     * @param port 서버 열 포트  (The port number on which the server will listen.)
     */
    private void server(
            final Class<?> sourceClass,
            final boolean isLocalhost,
            final int port
    ) {
        // check if port is valid | 포트번호 올바른지 검사
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        }

        // 컨테이너 초기화로 라우터까지 자동 생성 | init the Container and the Router
        final Container container = new Container();
        container.init(Scanner.scan(sourceClass)); // scan all classes

        // 디스패쳐 빈 가져오기 | get Dispatcher bean from container
        final Dispatcher dispatcher = container.getBean(Dispatcher.class);

        // 쓰레드 생성 | create new thread
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // open the socket for HTTP
        try (ServerSocket serverSocket = new ServerSocket()) {

            // build address information
            final InetSocketAddress inetSocketAddress;
            if (isLocalhost) {
                inetSocketAddress = new InetSocketAddress("127.0.0.1", port);
            } else {
                inetSocketAddress = new InetSocketAddress("0.0.0.0", port);
            }

            // bind the socket
            serverSocket.bind(inetSocketAddress);

            // start log | 시작 로그 찍기
            System.out.println("[INFO] Server started on port " + port + "...");

            // server working | 서버 작동
            while (true) {
                System.out.println("[INFO] Waiting for connection...");

                final Socket socket = serverSocket.accept();

                executor.submit(() -> {
                    System.out.println("[INFO] Client connected.");

                    try {

                        // 소켓 타입아웃 5초 | timeout == 5sec
                        socket.setSoTimeout(5000);

                        // input / output stream for http
                        final InputStream in = new BufferedInputStream(socket.getInputStream());
                        final OutputStream out = new BufferedOutputStream(socket.getOutputStream());

                        // if socket is alive, use same stream == keep-alive
                        while (!socket.isClosed()) {
                            // parse raw HTTP request
                            final HttpRequest httpRequest = HttpRequestParser.parse(in);

                            // send the HTTP request and invoke the appropriate method.
                            final HttpResponse httpResponse = dispatcher.dispatch(httpRequest);

                            // if there is some HTTP response, it will respond.
                            if (httpResponse != null) {
                                out.write(httpResponse.toBytes());
                                out.flush();
                            }

                            // check the Connection HTTP header
                            final String connectionHeaderValue = httpResponse.getHeader().getHeaders().get("connection").getFirst();

                            // if it is close, break the loop and release streams.
                            if ("close".equalsIgnoreCase(connectionHeaderValue)) {
                                break;
                            }
                        }
                    }
                    // socket timeout
                    catch (SocketTimeoutException ex) {
                        System.out.println("[WARN] Keep-alive timeout occurred.");
                    }
                    catch (IOException ex) {
                        System.err.println("[ERROR] IOException occurred during client communication.");
                        ex.printStackTrace();
                    }
                    finally {
                        // release socket
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            System.err.println("[ERROR] Failed to close socket.");
                            ex.printStackTrace();
                        }
                    }
                });
            }

        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            // release thread
            executor.shutdown();
        }
    }
}
