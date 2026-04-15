package com.seohamin.jastapi_example.post.dto;

import com.seohamin.jastapi_example.post.entity.Post;

import java.util.List;

public class PostListResponse {
    private List<PostResponse> postResponses;

    public PostListResponse(List<Post> posts) {
        this.postResponses = posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    public List<PostResponse> getPostResponses() {
        return postResponses;
    }
}
