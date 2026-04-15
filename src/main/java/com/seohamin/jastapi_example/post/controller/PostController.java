package com.seohamin.jastapi_example.post.controller;

import com.seohamin.jastapi.annotation.*;
import com.seohamin.jastapi_example.post.dto.PostListResponse;
import com.seohamin.jastapi_example.post.dto.PostRequest;
import com.seohamin.jastapi_example.post.dto.PostResponse;
import com.seohamin.jastapi_example.post.service.PostService;

/**
 * This class provides CRUD of post.
 */
@Component
public class PostController {

    // instance variable for DI
    private final PostService postService;

    // constructor for DI
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Gets all the post where in the DB.
     * @return PostListResponse consists with PostResponse
     */
    @Get("/api/post")
    public PostListResponse getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * Create a new post.
     * @param postRequest new post's information.
     * @return post's information that created.
     */
    @Post("/api/post")
    public PostResponse createPost(
            @RequestBody PostRequest postRequest
    ) {
        return postService.createPost(postRequest);
    }

    /**
     * Gets a specific post with post's id.
     * @param id post's id that want to find.
     * @return a specific post's information.
     */
    @Get("/api/post/{id}")
    public PostResponse getPost(
            @PathVariable("id") String id
    ) {
       return postService.getPost(id);
    }

    /**
     * Updates the information of a specific post.
     * @param id post's id that want to update.
     * @param postRequest post's information.
     * @return post's updated information
     */
    @Patch("/api/post/{id}")
    public PostResponse updatePost(
            @PathVariable("id") String id,
            @RequestBody PostRequest postRequest
    ) {
        return postService.updatePost(id, postRequest);
    }

    /**
     * Deletes a specific post with post's id.
     * @param id post's id that want to delete.
     * @param postRequest post's information. (It required because of checking password)
     */
    @Delete("/api/post/{id}")
    public void deletePost(
            @PathVariable("id") String id,
            @RequestBody PostRequest postRequest
    ) {
        postService.deletePost(id, postRequest);
    }
}
