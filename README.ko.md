# JastAPI

[[English]](./README.md) | [한국어]

## 프로젝트 제목
**백엔드 프레임워크 `JastAPI`와 게시판 CRUD 앱**

## 팀 멤버
| 이름  | 학번       | 담당                                  |
|:----|:---------|:------------------------------------|
| 서하민 | 22300378 | 백엔드 프레임워크 개발, 게시판 CRUD 앱 구조 설계 및 구현 |
| 손동현 | 22300385 | 게시판 CRUD 앱 구현                       |

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

## 4. 게시판 CRUD 앱 실행 가이드

아래 가이드는 `JastAPI`의 예제 프로젝트인 게시판 CRUD 프로젝트를 실행시키는 가이드입니다.
만약 간단하게 이용하고 싶으시다면 아래 링크를 통해 배포 중인 프로젝트를 이용할 수 있습니다.

> <a href="https://jastapi.seohamin.com/">https://jastapi.seohamin.com/</a>
> 
> *본 프로젝트에서 비밀번호는 평문으로 저장됩니다. 절대로 실제 비밀번호를 입력하지 마세요.

### 4.1 사전 준비

- **Java**: JDK 21
- **Database**: MariaDB
> 만약 JDK 21보다 상위 버전의 JDK를 이용하고 있을 경우, `build.gradle`의 `JavaLanguageVersion.of(21)`부분을 `JavaLanguageVersion.of(자신의 버전)`으로 교체하면 정상적으로 이용 가능합니다.

> MariaDB를 설치 하지 않아도 서버를 구동시 `index.html` 서빙까지는 작동합니다. 

### 4.2 데이터베이스 설정

애플리케이션을 실행하기 전, 로컬에서 MariaDB가 실행 중이어야 하며, 아래 접속 정보에 맞게 데이터 베이스 유저를 생성하거나,
`MariaDbConnectionProvider.java`에서 접속 유저의 정보를 적절히 수정해야 합니다.

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

### 4.3 서버 실행 방법
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

### 4.4 서비스 접속 및 이용

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

## 5. UML 다이어그램

<img src="./docs/images/flow.png" width="700" alt="flow" />
<img src="./docs/images/uml.png" width="1000" alt="uml" />

