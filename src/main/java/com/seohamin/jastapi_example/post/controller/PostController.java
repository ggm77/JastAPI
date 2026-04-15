package com.seohamin.jastapi_example.post.controller;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.annotation.web.*;
import com.seohamin.jastapi_example.post.dto.PostListResponse;
import com.seohamin.jastapi_example.post.dto.PostRequest;
import com.seohamin.jastapi_example.post.dto.PostResponse;
import com.seohamin.jastapi_example.post.service.PostService;

@Component
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 전체 조회
    @Get("/api/post")
    public PostListResponse hello() {
        return postService.getAllPosts();
    }

    //게시글 등록
    @Post("/api/post")
    public PostResponse post(
            @RequestBody PostRequest postRequest
            ) {
        return postService.createPost(postRequest);
    }

    //특정 게시글 조회
    @Get("/api/post/{id}")
    public PostResponse get(
            @PathVariable("id") String id
    ) {
       return postService.getPost(id);
    }
    //게시글 수정
    @Patch("/api/post/{id}")
    public PostResponse patch(
            @PathVariable("id") String id,
            @RequestBody PostRequest postRequest
    ) {
        return postService.updatePost(id, postRequest);
    }

    //게시글 삭제
    @Delete("/api/post/{id}")
    public String delete(
            @PathVariable("id") String id,
            @RequestBody PostRequest postRequest
    ) {
        postService.deletePost(id, postRequest);
        return "deleted";
    }
}
