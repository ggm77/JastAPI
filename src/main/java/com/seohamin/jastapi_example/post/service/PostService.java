package com.seohamin.jastapi_example.post.service;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi_example.post.dto.PostListResponse;
import com.seohamin.jastapi_example.post.dto.PostRequest;
import com.seohamin.jastapi_example.post.dto.PostResponse;
import com.seohamin.jastapi_example.post.entity.Post;
import com.seohamin.jastapi_example.post.repository.PostRepository;

/**
 * The business class for post.
 */
@Component
public class PostService {

    // instance variable for DI
    private final PostRepository postRepository;

    // constructor for DI
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Gets all post and return as PostListResponse
     * @return post's information list
     */
    public PostListResponse getAllPosts() {
        return new PostListResponse(postRepository.findAll());
    }

    /**
     * Gets a single post with id.
     * @param rawId the post's id
     * @return a post's information
     */
    public PostResponse getPost(String rawId){
        return new PostResponse(postRepository.findById(Long.valueOf(rawId)));
    }

    /**
     * Creates a new post.
     * @param postRequest the new post's information.
     * @return created post's information.
     */
    public PostResponse createPost(PostRequest postRequest){
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), postRequest.getAuthor(), postRequest.getPassword());

        // set id with post's PK
        post.setId(postRepository.save(post));

        return new PostResponse(post);
    }

    /**
     * Updates post's information.
     * @param rawId a post's id that wants to update.
     * @param postRequest a post's new information to update.
     * @return updated post's information.
     */
    public PostResponse updatePost(String rawId, PostRequest postRequest) {
        Post post = postRepository.findById(Long.valueOf(rawId));

        // checks the password
        if (!post.getPassword().equals(postRequest.getPassword())) {
            throw new RuntimeException("wrong password."); // it will return 400
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthor(postRequest.getAuthor());

        return new PostResponse(postRepository.update(post));
    }

    /**
     * Deletes the post.
     * @param rawId a post's id that wants to delete.
     * @param postRequest a post's information. (It required because of password)
     */
    public void deletePost(String rawId, PostRequest postRequest) {
        Long id = Long.valueOf(rawId);

        // 1. 우선 해당 ID로 게시글을 찾아오기
        Post post = postRepository.findById(id);

        // 2. 클라이언트가 보낸 비번(postRequest)과 게시글 비번(post.getPassword) 비교
        if (post.getPassword().equals(postRequest.getPassword())) {
            // 비번이 맞으면 삭제 진행
            postRepository.delete(id);
        } else {
            // 비번이 틀리면 에러 발생
            throw new RuntimeException("wrong password."); // it will return 400
        }
    }
}
