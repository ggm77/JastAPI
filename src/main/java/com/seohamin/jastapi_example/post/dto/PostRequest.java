package com.seohamin.jastapi_example.post.dto;

public class PostRequest {
    //게시글 등록시에 클라이언트가 줘야 하는 정보
        //title content author password
        private String title;
        private String content;
        private String author;
        private String password;

        // Jackson 역직렬화를 위한 기본 생성자 필수
        public PostRequest() {}

        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getAuthor() { return author; }
        public String getPassword() { return password; }

}
