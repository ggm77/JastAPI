package com.seohamin.jastapi_example.post.entity;

public class Post {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String password;

    public Post(Long id, String title, String content, String author, String password) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getPassword() { return password; }

    // 게시글 수정을 위한 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
