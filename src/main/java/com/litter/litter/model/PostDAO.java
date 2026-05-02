package com.litter.litter.model;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class PostDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void insertPost(Post post) {
        String sql = "INSERT INTO POSTS(content, user_id)"
                + " VALUES (?,?::uuid)";
        Object[] obj = new Object[2];

        obj[0] = post.getContent();
        obj[1] = "2149ed40-557b-495c-91cc-857cf7526d64";

        jdbc.update(sql, obj);
    }

    public void deletePost(String uuid) {
        String sql = "DELETE FROM POSTS WHERE ID=?::uuid";
        jdbc.update(sql, uuid);
    }

    public Post showPost(String uuid) {
        String sql = "SELECT * FROM POSTS WHERE ID=?::uuid";
        return Post.convert(jdbc.queryForMap(sql, uuid));
    }

    public ArrayList<Post> listPosts() {
        String sql = "SELECT POSTS.CONTENT, USERS.HANDLE, POSTS.USER_ID, USERS.USERNAME, USERS.PICTURE, POSTS.CREATED_AT FROM POSTS JOIN USERS ON POSTS.USER_ID = USERS.ID";
        return Post.convertAll(jdbc.queryForList(sql));
    }

    public ArrayList<Post> listUserPosts(String uuid) {
        String sql = "SELECT CONTENT, USERS.HANDLE, USERS.USERNAME, POSTS.CREATED_AT, USERS.PICTURE FROM POSTS JOIN USERS ON POSTS.USER_ID = USERS.ID WHERE USERS.ID = ?::uuid";
        return Post.convertAll(jdbc.queryForList(sql, uuid));
    }
}
