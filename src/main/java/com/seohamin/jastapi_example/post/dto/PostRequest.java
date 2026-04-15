package com.seohamin.jastapi_example.post.dto;

/**
 * This class is the form of http request body that using for post CRUD APIs.
 */
public class PostRequest {
    //게시글 등록시에 클라이언트가 줘야 하는 정보 | required information when create post.
    private String title;
    private String content;
    private String author;
    private String password;

    // Jackson 역직렬화를 위한 기본 생성자 필수 | default constructor may needed because of ObjectMapper(Jackson).
    public PostRequest() {}

    // getter for instance variables.
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getPassword() { return password; }

}
