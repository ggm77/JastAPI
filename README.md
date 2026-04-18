# JastAPI

[English] | [[한국어]](./README.ko.md)

_*Note: The Korean version is the original document and may offer the most natural phrasing._

## Project Title
**Backend Framework `JastAPI` and Board CRUD App**

## Team Members
| Name                    | Student ID       | Role                                  |
|:----------------------|:---------|:------------------------------------|
| Hamin SEO (서하민)       | 22300378 | Backend framework development, Board CRUD app architecture design and implementation |
| Donghyeon SON (손동현)   | 22300385 | Board CRUD app implementation                       |

## Development Version and Dependencies
- **Java**: `21`
- **Build Tool**: `Gradle 9.4.0`
- **Dependencies**: `Jackson Databind 2.21.2`

## 1. Project Overview

`JastAPI` is a backend framework that allows you to build backend servers in Java as easily and intuitively as FastAPI.
It is heavily inspired by Spring Boot.
It features a built-in WAS, enabling you to run the server with just a single line of code without complex external configurations.

This project is mainly composed of two parts: the framework core, `JastAPI`, and a demo application utilizing it, `jastapi_example`.

## 2. JastAPI (Backend Framework)
Implemented with inspiration from Spring Boot's operating mechanism,
it supports automatic class scanning and Dependency Injection (DI) based on reflection.

### 2.1 Key Features and Characteristics

- **Built-in WAS**: Through a built-in WAS implemented using `ServerSocket`,
the server runs simply by calling the `JastApiApplication.run()` method without any external configuration.
- **Automatic Class Scanning and DI Container**: When the server starts,
it automatically scans classes under a specific package,
registers classes with the `@Component` annotation as singleton Beans in the container,
and injects the necessary dependencies.
- **Annotation-based Routing**: Supports intuitive routing tailored to HTTP methods through
`@Get`, `@Post`, `@Patch`, and `@Delete` annotations.
Additionally, it can parse appropriate values from HTTP requests via
`@RequestBody`, `@RequestParam`, and `@PathVariable` annotations.
- **Automatic Data Conversion**: Utilizes `Jackson Databind` to automatically
serialize and deserialize incoming JSON requests from the client into Java objects, and Java objects into JSON responses.

### 2.2 Internal Structure

> Package scanning and bean registration process during server startup.
<img src="./docs/images/serverInit.png" width="700" alt="Initialization Flow" />

> Processing flow when a request is received.
<img src="./docs/images/backendFlow.png" width="700" alt="Runtime Flow" />

## 3. Board CRUD App
This is a sample project demonstrating how the `JastAPI` framework can actually be utilized.
It implements basic post creation, reading, updating, and deletion (CRUD) features.
It also persists data by integrating with a DB.

### 3.1 Key Components

- **Main.java**: The entry point of the application,
running the server through the `JastApiApplication.run()` method.
- **HomeController.java**: Serves the index.html file, which acts as the frontend when accessing the root path (`/`).
- **PostController.java**: Provides the `/api/post` endpoint, receives HTTP requests, and passes them to the service layer.
- **PostService.java**: Generates responses according to various logic based on the HTTP request information passed down from the controller.
- **MariaDbConnectionProvider.java & PostRepository.java**: Responsible for database connection and manipulating actual data.

## 4. Board CRUD App Execution Guide

The following guide is for running the Board CRUD project, an example project of `JastAPI`.
If you want to use it simply, you can access the deployed project via the link below.

> <a href="https://jastapi.seohamin.com/">https://jastapi.seohamin.com/</a>
>
> *In this project, passwords are saved as plaintext. Never enter a real password.

### 4.1 Prerequisites

- **Java**: JDK 21
- **Database**: MariaDB
> If you are using a JDK version higher than 21, you can use it normally by replacing the `JavaLanguageVersion.of(21)` part in `build.gradle` with `JavaLanguageVersion.of(your_version)`.

> Even if you do not install MariaDB, the server will operate up to serving `index.html` when started.

### 4.2 Database Configuration

Before running the application, MariaDB must be running locally.
You need to create a database user according to the connection information below,
or appropriately modify the connecting user's information in `MariaDbConnectionProvider.java`.

- **URL**: `jdbc:mariadb://localhost:3306/jastapi_example`
- **User ID**: `jastapi`
- **User Password**: `1234`

Access the database to create the `jastapi_example` schema according to the settings above, and create the `post` table to run the example.
You can create it using the code below.
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

### 4.3 How to Run the Server
> 1. Run the following command in the root directory of the project (the folder containing the README.md file).
> 
> `./gradlew build`


> 2. Navigate to the location of the generated JAR file using the command below.
> 
> `cd build/libs`

> 3. Run the server using the command below. (You can terminate it with `^C` or `Ctrl + C`.)
> 
> `java -jar JastAPI-1.0.0.jar`

> 4. If the message `[INFO] Server started on port 8080...` is printed in the console, the server has started successfully.
> <img src="./docs/images/serverlog.png" width="700"  alt="serverLog"/>

### 4.4 Service Access and Usage

> 1. Open a web browser and enter `http://localhost:8080` in the address bar.

> 2. You can register, view, edit, or delete posts through the UI displayed on the screen.
> <img src="./docs/images/main.png" width="700" alt="mainPage" />

### _**In this project, passwords are stored in plaintext. Never enter a real password.**_

> 3. As shown in the image below, enter the post title, author, password, and content, then click the `save` button to save it.
> <img src="./docs/images/createPost.png" width="300" alt="createPost" />
> <img src="./docs/images/createPostResult.png" width="300" alt="createPost" />

> 4. You can check the changes in the list below, and click on the title to view the content.
> <img src="./docs/images/createPostResultList.png" width="300" alt="createPostResultList" />
> <img src="./docs/images/createPostResultContent.png" width="300" alt="createPostResultList" />

> 5. Clicking the `edit` button in the post list loads the current post content. You can modify it by making appropriate changes and entering the correct password.
> <img src="./docs/images/editPost.png" width="300" alt="editPost" />
> <img src="./docs/images/editPostResult.png" width="300" alt="editPostResult" />

> 6. You can delete a post by clicking the `delete` button in the post list and entering the correct password.
> <img src="./docs/images/deletePost.png" width="300" alt="deletePost" />
> <img src="./docs/images/deletePostResult.png" width="300" alt="deletePostResult" />

> 7. If an invalid value is entered during post registration, modification, or deletion, a 400 error occurs.
> <img src="./docs/images/400.png" width="300" alt="HTTP400" />

## 5. UML Diagrams

### 5.1 JastAPI

### 5.2 Board CRUD App

