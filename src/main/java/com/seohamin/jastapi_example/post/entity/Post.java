package com.seohamin.jastapi_example.post.entity;

/**
 * This class is matched 1:1 with DB's table.
 * It used when access DB.
 */
public class Post {

    // instance variables == DB's columns
    private Long id;
    private String title;
    private String content;
    private String author;
    private String password;

    /**
     * Constructor that only without id.
     * @param title title to set
     * @param content content to set
     * @param author author to set
     * @param password password to set
     */
    public Post(String title, String content, String author, String password) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.password = password;
    }

    /**
     * All-arguments constructor
     * @param id id to set
     * @param title title to set
     * @param content content to set
     * @param author author to set
     * @param password password to set
     */
    public Post(Long id, String title, String content, String author, String password) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.password = password;
    }

    // getters for instance variables.
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getPassword() { return password; }

    // setters for instance variables.
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
