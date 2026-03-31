# JastAPI

## Team Members
| Name | Student ID | Responsibilities |
| :--- | :--- | :--- |
| Hamin SEO (서하민) | 22300378 | Framework Core |
| Donghyeon SON (손동현) | 22300385 | Sample Board CRUD App |

## Project Title
**JastAPI: Simple Java Backend Framework**

## Version
- **Java**: 21
- **Build Tool**: Gradle 9.4.0
- **Dependencies**: Jackson Databind 2.21.2

## Description

JastAPI is a backend framework that makes building backend servers in Java as easy and intuitive as FastAPI. It was developed with significant influence from Spring Boot. The framework features a built-in WAS, allowing you to run a server with just a single line of code without the need for complex configurations. It provides an object-oriented singleton development environment through reflection-based automatic class scanning and a DI container. Furthermore, the project includes a sample board CRUD application that demonstrates the practical use of the framework.

JastAPI는 Java에서 FastAPI처럼 쉽고 직관적으로 백엔드 서버를 구축할 수 있게 하는 백엔드 프레임워크입니다. Spring Boot로부터 많은 영향을 받았습니다. 복잡한 설정 없이 단 한 줄의 코드로 서버를 구동할 수 있도록 자체 WAS를 제공합니다. 리플랙션 기반의 자동 클래스 스캔과 DI 컨테이너를 통해 객체지향적인 싱글톤 개발 환경을 제공합니다. 또한 해당 프레임워크의 실제 활용법을 보여주는 게시글 CRUD 예제 프로젝트가 포함됩니다.


### 1. Initialization Phase (Startup)
> Package scanning and bean registration process upon server startup.
> 
> 서버 시작 시 패키지 스캔 및 빈 등록 과정입니다.
<img src="https://github.com/user-attachments/assets/8c9691aa-7761-486c-b7d2-b57e8fb61b9f" width="700" alt="Initialization Flow" />

### 2. Request Handling Phase (Runtime)
> Request handling and processing flow.
>
> 요청이 들어왔을 때의 처리 흐름입니다.
<img src="https://github.com/user-attachments/assets/2a586247-5e55-4c48-ba8c-33b1b34ac89c" width="700" alt="Runtime Flow" />


