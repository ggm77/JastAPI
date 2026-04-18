# JastAPI

[[English]](./README.md) | [한국어]

## 프로젝트 제목
**백엔드 프레임워크 `JastAPI`와 게시판 CRUD 앱**

## 팀 멤버
| 이름  | 학번       | 담당                                  |
|:----|:---------|:------------------------------------|
| 서하민 | 22300378 | 백엔드 프레임워크 개발, 게시판 CRUD 앱 구조 설계 및 구현 |
| 손동현 | 22300385 | 게시판 CRUD 앱 구현                       |

> **유튜브 발표 영상**
> 
> <a href="https://youtu.be/4t3AXDRdrkw">https://youtu.be/4t3AXDRdrkw</a>
> 
> 영상 설명이 부족하게 느껴지는 부분이 있다면, README.md에 기재된 내용을 확인해 주세요.

## 개발 버전과 의존성
- **Java**: `21`
- **Build Tool**: `Gradle 9.4.0`
- **Dependencies**: `Jackson Databind 2.21.2`

## 1. 프로젝트 개요

`JastAPI`는 Java에서 FastAPI처럼 쉽고 직관적으로 백엔드 서버를 구축할 수 있게 하는 백엔드 프레임워크입니다.
Spring Boot로부터 많은 영향을 받았습니다.
복잡한 외부 설정 없이 단 한 줄의 코드로 서버를 구동할 수 있도록 자체 WAS를 내장하고 있습니다.

본 프로젝트는 크게 프레임워크 코어인 `JastAPI`와
이를 활용한 데모 애플리케이션인 `jastapi_example` 두 부분으로 구성되어있습니다.

## 2. JastAPI (백엔드 프레임워크)
Spring Boot의 동작 방식에서 영감을 받아 구현되었으며,
리플렉션을 기반으로 한 자동 클래스 스캔과 의존성 주입(DI)을 지원합니다.

### 2.1 주요 기능 및 특징

- **자체 WAS 내장**: `ServerSocket`을 활용해 구현된 내장 WAS를 통해 외부 설정 없이
`JastApiApplication.run()`메서드 호출만으로 서버가 실행됩니다.
- **자동 클래스 스캔 및 DI 컨테이너**: 서버 구동시 자동으로 특정 패키지의 하위에 존재하는 클래스들을
스캔해서 `@Component` 어노테이션이 붙은 클래스들을 싱글톤 빈(Bean)으로 컨테이너에 자동으로 등록하고,
필요한 의존성을 주입합니다.
- **어노테이션 기반 라우팅**: `@Get`, `@Post`, `@Patch`, `@Delete` 어노테이션을 통해 HTTP 메서드에 맞춘
직관적인 라우팅을 지원합니다. 또한 `@RequestBody`, `@RequestParam`, `@PathVariable` 어노테이션을 통해
HTTP응답에서 적절한 값을 파싱할 수 있습니다.
- **자동 데이터 변환**: `Jackson Databind`를 활용하여 클라이언트로부터 들어오는 JSON 요청을 Java 객체로, Java 객체를 JSON 응답으로 자동으로
직렬화 / 역직렬화합니다.

### 2.2 내부 구조

> 서버 시작 시 패키지 스캔 및 빈 등록 과정입니다.
<img src="./docs/images/serverInit.png" width="700" alt="Initialization Flow" />

> 요청이 들어왔을 때의 처리 흐름입니다.
<img src="./docs/images/backendFlow.png" width="700" alt="Runtime Flow" />


## 3. 게시판 CRUD 앱
`JastAPI` 프레임워크가 실제로 어떻게 활용 될 수 있는지 보여주는 샘플 프로젝트입니다.
기본적인 게시글 추가, 조회, 수정, 삭제(CRUD) 기능이 구현되어 있습니다.
또한 DB와 연동하여 데이터를 영속화합니다.

### 3.1 주요 구성 요소

- **Main.java**: 애플리케이션의 진입점으로서 `JastApiApplication.run()` 메서드를 통해서
서버를 작동시킵니다.
- **HomeController.java**: 루트 경로(`/`)로 접속시 프론트엔드 역할을 하는 `index.html` 파일을 서빙합니다.
- **PostController.java**: `/api/post` 엔드포인트를 제공하며, HTTP 요청을 받아 서비스 레이어로 전달합니다.
- **PostService.java**: 컨트롤러에서 내려온 HTTP 요청 정보를 바탕으로 각종 로직에 따라 응답을 생성합니다.
- **MariaDbConnectionProvider.java & PostRepository.java**: 데이터베이스와 연결을
담당하고, 실질적인 데이터를 조작합니다.

## 4. JastAPI 백엔드 프레임워크 사용 가이드
`JastAPI`는 직관적인 어노테이션을 통해 간편하게 서버를 구현 할 수 있게 하는걸 목표로 개발되었습니다.
또한, 본 백엔드 프레임워크는 Spring Boot에서 많은 영향을 받았기 때문에 대부분의 사용
방법이 유사합니다.

### 4.1 사전 준비
본 프레임워크의 클래스들은 모두 `src/main/java/com/seohamin/jastapi` 하위에 있습니다.
따라서 `java` 폴더 아래에 `com` 패키지부터 통째로 복사하여 프로젝트의 적절한 곳에 배치한 후,
`com.seohamin` 패키지 하위에 존재하는 예제 프로젝트인 `jastapi_example` 패키지를 삭제하는 것을 권장합니다.

또한 이 프레임워크는 `Jackson Databind 2.21.2`를 필요로 합니다. 프로젝트의 의존성에 추가되어야 합니다.

### 4.2 기본 서버 동작
`JastAPI`는 자체 WAS를 이용하므로 아래 코드를 통해 즉시 서버를 실행 시킬 수 있습니다.
```java
import com.seohamin.jastapi.JastApiApplication;

public class Main {
    public static void main(String[] args) {
        // 첫 번째 인자: 패키지 스캔의 기준이 되는 클래스 - 이 클래스가 포함된 패키지의 하위에 존재하는 클래스들을 스캔합니다.
        // 두 번째 인자: 로컬호스트(127.0.0.1) 전용 바인딩 여부 (false면 0.0.0.0)
        // 세 번째 인자: 사용할 포트 번호
        JastApiApplication.run(Main.class, false, 8080);
    }
}
```

### 4.3 의존성 주입 (DI)
`JastAPI`는 서버 구동시 프로젝트를 스캔하며 `@Component` 어노테이션이 달린 클래스를 찾아,
싱글톤 빈(Bean)으로 관리합니다. 생성자를 통해 필요한 객체를 자동으로 주입 받을 수 있습니다.
**단, 의존성 주입을 받을 클래스는 단 하나의 생성자만 가지고 있어야 합니다.**
```java
import com.seohamin.jastapi.annotation.core.Component;

@Component
public class PostService {
    
    // 주입 받은 객체를 저장할 필드
    private final PostRepository postRepository;
    
    // 생성자를 통한 DI
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    // 예시
    public void printPost(Long id) {
        // 주입받은 객체의 메서드를 바로 사용가능
        System.out.println(postRepository.findById(id));
    }
}
```

`JastAPI`에서도 인터페이스를 정의하고, 해당 인터페이스만 주입 받아 어떤 구현체를 사용하는지 숨길 수 있습니다.
단, 구현체가 여러개일 경우 어떤 구현체를 주입해야 할지 정하지 못하기 때문에, 현재는 인터페이스당 하나의 구현체만 존재할 수 있습니다.

```java
// MariaDbConnectionProvider.java

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.db.ConnectionProvider;

import java.sql.Connection;

@Component
public class MariaDbConnectionProvider implements ConnectionProvider {
    @Override
    public Connection getConnection() {
        // 생략..
    }

    @Override
    public void releaseConnection(Connection connection) {
        // 생략..
    }
}

// PostRepository.java
import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.db.ConnectionProvider;

import java.sql.Connection;

@Component
public class PostRepository {

    // 객체 주입받을 필드
    private final ConnectionProvider connection;

    // 생성자를 통해 DI
    public PostRepository(ConnectionProvider connectionProvider) {
        this.connection = connectionProvider;
    }

    // MariaDbConnectionProvider에서 오버라이딩한 메서드를 자동으로 사용
    private Connection getConnection() {
        return connection.getConnection();
    }
}
```


### 4.4 라우팅 설정
`@Component`를 통해 빈으로 등록된 클래스 내부에서,
HTTP 메서드에 맞는 어노테이션(`@Get`, `@Post`, `@Patch`, `@Delete`)을 사용하여 라우팅을 설정합니다.

```java
@Get("/api/post/{id}")
public PostResponse getPost(
        @PathVariable("id") String id
) {
    return postService.getPost(id);
}
```

### 4.5 클라이언트 요청 파싱
`@PathVariable`, `@RequestParam`, `@RequestBody` 어노테이션을 통해서
클라이언트가 전송하는 요청을 자동으로 파싱할 수 있습니다.
- **`@PathVariable`**: URL 경로에 포함된 동적 값을 추출합니다. (동시에 여러개 사용가능)
- **`@RequestParam`**: URL의 쿼리 스트링(Query String) 값을 추출합니다.
- **`@RequestBody`**: HTTP Body로 들어오는 JSON 데이터를 자바 객체로 역직렬화합니다.

```java
@Patch("/api/post/{id}/content/{contentId}")
public PostResponse updatePost(
        @PathVariable(value="id") String id, // 어노테이션의 value는 꼭 엔드포인트에 적힌 값과 같아야 함.
        @PathVariable(value="contentId") String contentId, // PathVariable은 String으로만 받을 수 있음.
        @RequestParam(value="query") List<String> query, // RequestParam은 List<String>으로만 받을 수 있음.
        @RequestBody PostRequest postRequest // HTTP request body를 PostRequest에 담아서 줌
) {
    return postService.updatePost(id, postRequest);
}
```

### 4.6 HttpRequest & HttpResponse
컨트롤러에서 `HttpRequest`를 매개변수로 가진 경우, 자동으로 클라이언트에게 받은 요청을 그대로 전달합니다.
컨트롤러에서 리턴 타입이 `HttpResponse`인 경우, 개발자가 리턴한 `HttpResponse`를 다른 추가 가공 없이 그대로 리턴합니다.

```java
@Get("/")
public HttpResponse getHomePage(HttpRequest httpRequest) {
    byte[] body = httpRequest.getBody();
    
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
```

### 4.7 직렬화와 역직렬화
컨트롤러의 리턴 타입이 일반 자바 객체이거나, `@RequestBody`가 정의된 변수의 타입이 일반 자바 객체인 경우,
각각 자동으로 직렬화, 역직렬화를 진행합니다.
이때 `Jackson Databind`를 통해서 변환 되기 때문에, 꼭 기본 생성자와 각 필드에 대한 게터가 존재해야합니다.
```java
public class PostRequest {
    // 필드
    private String title;
    private String content;
    private String author;
    private String password;

    // 직렬화를 위한 기본 생성자
    public PostRequest() {}

    // 직렬화를 위한 게터
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getPassword() { return password; }

}
```

### 4.8 예외 처리
`HttpResponseException` 클래스를 이용하면, 비즈니스 로직에서 바로 400번대나 500번대 HTTP 응답코드를 가진 응답을 전송시킬 수 있습니다.
이외에 일반 예외 (NPE 등)이 발생하면 디스패쳐에서 500 INTERNAL_SERVER_ERROR로 래핑되어서 응답합니다.

```java
throw new HttpResponseException(ErrorResponse.createBadRequest("HTTP/1.1"));
```

## 5. 게시판 CRUD 앱 실행 가이드

아래 가이드는 `JastAPI`의 예제 프로젝트인 게시판 CRUD 프로젝트를 실행시키는 가이드입니다.
만약 간단하게 이용하고 싶으시다면 아래 링크를 통해 배포 중인 프로젝트를 이용할 수 있습니다.

> <a href="https://jastapi.seohamin.com/">https://jastapi.seohamin.com/</a>
> 
> *본 프로젝트에서 비밀번호는 평문으로 저장됩니다. 절대로 실제 비밀번호를 입력하지 마세요.

### 5.1 사전 준비

- **Java**: JDK 21
- **Database**: MariaDB
> 만약 JDK 21보다 상위 버전의 JDK를 이용하고 있을 경우, `build.gradle`의 `JavaLanguageVersion.of(21)`부분을 `JavaLanguageVersion.of(자신의 버전)`으로 교체하면 정상적으로 이용 가능합니다.

> MariaDB를 설치 하지 않아도 서버를 구동시 `index.html` 서빙까지는 작동합니다. 

### 5.2 데이터베이스 설정

애플리케이션을 실행하기 전, 로컬에서 MariaDB가 실행 중이어야 하며, 아래 접속 정보에 맞게 데이터 베이스 유저를 생성하거나,
`MariaDbConnectionProvider.java`에서 접속 유저의 정보를 적절히 수정해야 합니다.
(외부 설정 파일에서 연결 정보를 가지고 오게 하고 싶었으나, 시간이 부족하여 이렇게 구현되었습니다.)

- **URL**: `jdbc:mariadb://localhost:3306/jastapi_example`
- **유저 ID**: `jastapi`
- **유저 비밀번호**: `1234`

데이터베이스에 접근하여 위 설정에 맞게 `jastapi_example` 스키마를 생성하고,
예제 구동을 위한 `post` 테이블을 만들어야 합니다. 아래 코드를 통해 생성할 수 있습니다.
```sql
CREATE TABLE post(
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);
```

### 5.3 서버 실행 방법
> 1. 프로젝트의 루트 디렉토리(README.md 파일이 존재하는 폴더)에서 아래 명령어를 실행합니다.
> 
> `./gradlew build`


> 2. 아래 명령어를 통해 생성된 JAR 파일 위치로 이동합니다.
> 
> `cd build/libs`

> 3. 아래 명령어를 통해 서버를 실행합니다. (`^C` 또는 `Ctrl + C`를 통해 종료 할 수 있습니다.)
> 
> `java -jar JastAPI-1.0.0.jar`

> 4. 콘솔에 `Server started on port 8080...` 메세지가 출력된다면 정상적으로 서버가 구동된 것입니다.
> <img src="./docs/images/serverlog.png" width="700"  alt="serverLog"/>

### 5.4 서비스 접속 및 이용

> 1. 웹 브라우저를 열고 주소창에 `http://localhost:8080`을 입력합니다.


> 2. 화면에 표시된 UI를 통해 게시글을 등록하거나 조회, 수정, 삭제 할 수 있습니다.
> <img src="./docs/images/main.png" width="700" alt="mainPage" />

### _**본 프로젝트에서 비밀번호는 평문으로 저장됩니다. 절대로 실제 비밀번호를 입력하지 마세요.**_

> 3. 아래 사진과 같이 게시글의 제목, 글쓴이, 비밀번호, 내용을 작성하고 `save` 버튼을 누르면 저장됩니다.
> <img src="./docs/images/createPost.png" width="300" alt="createPost" />
> <img src="./docs/images/createPostResult.png" width="300" alt="createPost" />

> 4. 변경 사항은 아래 리스트에서 확인 할 수 있고, 제목을 누르면 내용을 확인 할 수 있습니다.
> <img src="./docs/images/createPostResultList.png" width="300" alt="createPostResultList" />
> <img src="./docs/images/createPostResultContent.png" width="300" alt="createPostResultList" />

> 5. 게시물 리스트에서 `edit` 버튼을 누르면 현재의 게시물 내용이 들어가고, 내용을 적절히 수정하고 비밀번호를 올바르게 작성하면
> 수정이 가능합니다.
> <img src="./docs/images/editPost.png" width="300" alt="editPost" />
> <img src="./docs/images/editPostResult.png" width="300" alt="editPostResult" />

> 6. 게시물 리스트에서 `delete` 버튼을 누르고 올바른 비밀번호를 입력하면 게시물 삭제가 가능합니다.
> <img src="./docs/images/deletePost.png" width="300" alt="deletePost" />
> <img src="./docs/images/deletePostResult.png" width="300" alt="deletePostResult" />

> 7. 만약 게시물 등록, 수정, 삭제 중에 적절하지 않은 값이 입력 된다면 400 에러가 발생합니다.
> <img src="./docs/images/400.png" width="300" alt="HTTP400" />

## 6. UML 다이어그램

> <img src="./docs/images/flow.png" width="700" alt="flow" />
> 점선: 의존관계
>
> 실선: 연관관계
> 
> 초록 블럭: 컨테이너가 관리하는 빈(Bean) (@Component를 통해 DI 됨)
>
> 흰색 블럭: 일반 자바 객체

> <img src="./docs/images/uml.png" width="1000" alt="uml" />
> 이미지를 클릭하면 원본 크기로 볼 수 있습니다.
