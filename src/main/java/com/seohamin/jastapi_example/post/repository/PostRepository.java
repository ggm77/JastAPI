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

/**
 * This class interact with DB and gets some results.
 */
@Component
public class PostRepository {

    // instance variable for DI
    private final ConnectionProvider provider; // it provides the connection with DB.

    // constructor for DI
    public PostRepository(ConnectionProvider connectionProvider) {
        this.provider = connectionProvider;
    }

    /**
     * Saves the post on DB.
     * @param post post to save.
     * @return saved post's id (PK).
     */
    public Long save(final Post post) {
        final String insertSql = "INSERT INTO post (title, content, author, password) VALUES (?,?,?,?)";
        final String findSql = "SELECT LAST_INSERT_ID()";

        // get connection
        try (final Connection conn = provider.getConnection()) {

            // save post's information
            try (PreparedStatement pst = conn.prepareStatement(insertSql)) {
                pst.setString(1, post.getTitle());
                pst.setString(2, post.getContent());
                pst.setString(3, post.getAuthor());
                pst.setString(4, post.getPassword());
                pst.executeUpdate();
            }

            // get post's id
            try (PreparedStatement pst = conn.prepareStatement(findSql)) {
                final ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        // default value
        return null;
    }

    /**
     * Gets a single post from DB.
     * @param id post's id
     * @return the post found with the given ID.
     */
    public Post findById(final Long id) {
        final String findByIdSql = "SELECT id, title, content, author, password FROM post WHERE id = ?";

        // get connection
        try (
                Connection conn = provider.getConnection();
                PreparedStatement pst = conn.prepareStatement(findByIdSql)
        ) {
            // set query
            pst.setLong(1, id);

            // execute the query and get result
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

    /**
     * Gets all posts from DB.
     * @return the posts found at DB.
     */
    public List<Post> findAll() {
        final String findAllSql = "SELECT id, title, content, author, password FROM post";
        final List<Post> posts = new ArrayList<>();

        // get connection
        try (
                Connection conn = provider.getConnection();
                PreparedStatement pst = conn.prepareStatement(findAllSql)
        ) {
            // execute the query and get result.
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // pare the result and add to array list
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

    /**
     * Updates a specific post's information.
     * @param post post's new information.
     * @return updated post.
     */
    public Post update(final Post post) {
        final String updateSql = "UPDATE post SET title=?, content=?, author=?, password=? WHERE id=?";

        // get connection with DB
        try (final Connection conn = provider.getConnection()) {

            // set query and execute the query
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

    /**
     * Deletes a specific post.
     * @param id post's id that wants to delete.
     * @return success or failure
     */
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
