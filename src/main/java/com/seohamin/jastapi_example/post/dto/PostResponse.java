package com.seohamin.jastapi_example.post.dto;

import com.seohamin.jastapi_example.post.entity.Post;

// 게시글 조회 응답용
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String author;

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
