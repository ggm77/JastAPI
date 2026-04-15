package com.seohamin.jastapi_example.post.repository;

import com.seohamin.jastapi.annotation.Component;
import com.seohamin.jastapi.db.ConnectionProvider;
import com.seohamin.jastapi_example.post.entity.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostRepository {

    private final ConnectionProvider provider;

    public PostRepository(ConnectionProvider connectionProvider) {
        this.provider = connectionProvider;
    }

    // Post 객체를 그대로 DB에 저장하는 메서드
    public Long save(final Post post) {
        final String insertSql = "INSERT INTO post (title, content, author, password) VALUES (?,?,?,?)";
        final String findSql = "SELECT LAST_INSERT_ID()";

        try (final Connection conn = provider.getConnection()) {

            try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                pst.setString(1, post.getTitle());
                pst.setString(2, post.getContent());
                pst.setString(3, post.getAuthor());
                pst.setString(4, post.getPassword());
                pst.executeUpdate();
            }

            try (PreparedStatement pst = conn.prepareStatement(findSql)) {
                final ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // id로 DB에서 게시글 조회하는 메서드
    public Post findById(final Long id) {
        final String findByIdSql = "SELECT id, title, content, author, password FROM post WHERE id = ?";

        try (
                Connection conn = provider.getConnection();
                PreparedStatement pst = conn.prepareStatement(findByIdSql)
        ) {
            pst.setLong(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    final Post post = new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getString("password")
                    );

                    return post;
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // DB에 저장된 모든 게시글 조회하는 메서드
    public List<Post> findAll() {
        final String findAllSql = "SELECT id, title, content, author, password FROM post";
        final List<Post> posts = new ArrayList<>();

        try (
                Connection conn = provider.getConnection();
                PreparedStatement pst = conn.prepareStatement(findAllSql)
        ) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    final Post post = new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getString("password")
                    );

                    posts.add(post);
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return posts;
    }

    // 인자로 받은 Post를 적절히 수정하는 메서드
    public Post update(final Post post) {
        final String updateSql = "UPDATE post SET title=?, content=?, author=?, password=? WHERE id=?";

        try (final Connection conn = provider.getConnection()) {
            try (final PreparedStatement pst = conn.prepareStatement(updateSql)) {
                pst.setString(1, post.getTitle());
                pst.setString(2, post.getContent());
                pst.setString(3, post.getAuthor());
                pst.setString(4, post.getPassword());
                pst.setLong(5, post.getId());

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    return post;
                }
            }


        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // 특정 id의 게시글을 삭제하는 메서드
    public boolean delete(final Long id) {
        final String deleteSql = "DELETE FROM post WHERE id=?";

        try (
                final Connection conn = provider.getConnection();
                final PreparedStatement pst = conn.prepareStatement(deleteSql)
        ) {
            pst.setLong(1, id);

            int rowAffected = pst.executeUpdate();

            if (rowAffected > 0) {
                return true;
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
