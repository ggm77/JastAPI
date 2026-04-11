package com.seohamin.jastapi_example.post.service;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi_example.post.dto.PostRequest;
import com.seohamin.jastapi_example.post.dto.PostResponse;
import com.seohamin.jastapi_example.post.entity.Post;

@Component
public class PostService {

    public PostResponse getPost(String rawId){
        return new PostResponse(new Post(Long.valueOf(rawId), "test", "content", "me", "1234"));
    }

    public PostResponse createPost(PostRequest postRequest){
        Post post = new Post(1L, postRequest.getTitle(), postRequest.getContent(), postRequest.getAuthor(), postRequest.getPassword());

        return new PostResponse(post);
    }
    public void deletePost(String rawId, PostRequest postRequest) {
        Long id = Long.valueOf(rawId);

        // 1. 우선 해당 ID로 게시글을 찾아오기
        Post post = new Post(id, "test", "content", "me", "1234");

        // 2. 클라이언트가 보낸 비번(postRequest)과 게시글 비번(post.getPassword) 비교
        if (post.getPassword().equals(postRequest.getPassword())) {
            // 비번이 맞으면 삭제 진행
            System.out.println(id + "post delete complete");
        } else {
            // 비번이 틀리면 에러 발생
            throw new RuntimeException("wrong password.");
        }
    }
    public PostResponse patchPost(){

    }
}
