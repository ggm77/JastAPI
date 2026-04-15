package com.seohamin.jastapi_example.post.dto;

import com.seohamin.jastapi_example.post.entity.Post;

import java.util.List;

/**
 * This class is the form of http response body that using for GET - /api/post API.
 */
public class PostListResponse {
    private List<PostResponse> postResponses;

    /**
     * Constructor with Post argument
     * @param posts the posts that using for this list's elements.
     */
    public PostListResponse(List<Post> posts) {
        this.postResponses = posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    // getter for postResponses (It required because of object mapper.)
    public List<PostResponse> getPostResponses() {
        return postResponses;
    }
}
