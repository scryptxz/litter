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
        obj[1] = post.getUser_id();


        jdbc.update(sql, obj);
    }

    public void insertReplyPost(Post post, String post_uuid) {
        String sql = "INSERT INTO POSTS(content, user_id, reply_post_id)"
                + " VALUES (?,?::uuid,?::uuid)";
        Object[] obj = new Object[3];

        obj[0] = post.getContent();
        obj[1] = post.getUser_id();

        obj[2] = post_uuid;

        jdbc.update(sql, obj);
    }

    public void updatePost(String uuid, String content) {
        String sql = "UPDATE POSTS SET CONTENT = ? WHERE UUID = ?::uuid";
        Object[] obj = new Object[] { content, uuid };
        jdbc.update(sql, obj);
    }

    public void deletePost(String uuid) {
        String sql = "DELETE FROM POSTS WHERE UUID=?::uuid";
        jdbc.update(sql, uuid);
    }


    public Post showPost(String uuid) {
        String sql = "SELECT P.UUID, P.CONTENT, U.HANDLE, P.USER_ID, U.USERNAME, U.PICTURE, P.CREATED_AT, P.REPLY_POST_ID, COUNT(R.UUID) AS REPLY_COUNT "
                + "FROM POSTS P JOIN USERS U ON P.USER_ID = U.ID LEFT JOIN POSTS R ON R.REPLY_POST_ID = P.UUID "
                + "WHERE P.UUID = ?::uuid "
                + "GROUP BY (P.UUID, U.HANDLE, U.USERNAME, U.PICTURE)";
        return Post.convert(jdbc.queryForMap(sql, uuid));
    }

    public ArrayList<Post> listPosts() {
        String sql = "SELECT P.UUID, P.CONTENT, U.HANDLE, P.USER_ID, U.USERNAME, U.PICTURE, P.CREATED_AT, P.REPLY_POST_ID, COUNT(R.UUID) AS REPLY_COUNT "
                + "FROM POSTS P JOIN USERS U ON P.USER_ID = U.ID LEFT JOIN POSTS R ON R.REPLY_POST_ID = P.UUID WHERE P.REPLY_POST_ID IS NULL "
                + "GROUP BY (P.UUID, U.HANDLE, U.USERNAME, U.PICTURE)"
                + "ORDER BY P.CREATED_AT";
        return Post.convertAll(jdbc.queryForList(sql));
    }

    public ArrayList<Post> listReplyPosts(String uuid) {
        String sql = "SELECT P.UUID, P.CONTENT, U.HANDLE, P.USER_ID, U.USERNAME, U.PICTURE, P.CREATED_AT, P.REPLY_POST_ID , COUNT(R.UUID) AS REPLY_COUNT "
                + "FROM POSTS P "
                + "JOIN USERS U ON P.USER_ID = U.ID "
                + "LEFT JOIN POSTS R ON R.REPLY_POST_ID = P.UUID "
                + "WHERE P.REPLY_POST_ID = ?::uuid "
                + "GROUP BY (P.UUID, U.HANDLE, U.USERNAME, U.PICTURE)";
        return Post.convertAll(jdbc.queryForList(sql, uuid));
    }

    public ArrayList<Post> listUserPosts(String handle) {
        String sql = "SELECT P.UUID, P.CONTENT, U.HANDLE, U.USERNAME, P.CREATED_AT, U.PICTURE, COUNT(R.UUID) AS REPLY_COUNT "
                + "FROM POSTS P "
                + "JOIN USERS U ON P.USER_ID = U.ID "
                + "LEFT JOIN POSTS R ON R.REPLY_POST_ID = P.UUID "
                + "WHERE U.HANDLE = ? AND P.REPLY_POST_ID IS NULL "
                + "GROUP BY(P.UUID, P.CONTENT, U.HANDLE, U.USERNAME, P.CREATED_AT, U.PICTURE)";
        return Post.convertAll(jdbc.queryForList(sql, handle));
    }
}
