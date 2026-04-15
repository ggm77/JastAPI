package com.seohamin.jastapi_example.post.dto;

import com.seohamin.jastapi_example.post.entity.Post;

/**
 * This class is the form of http response body that using for post CRUD APIs.
 */
public class PostResponse {
    // the information that client may receive.
    private Long id;
    private String title;
    private String content;
    private String author;

    /**
     * Constructor with Post
     * @param post the class that includes post's information
     */
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
    }

    // Jackson 직렬화를 위한 Getter 필수
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
}
