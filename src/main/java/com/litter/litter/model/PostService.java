package com.litter.litter.model;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserService userService;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // With Spring Security configured, auth.getName() is USERS.HANDLE
        String handle = auth.getName();
        return userService.showUser(handle);
    }

    public void insertPost(Post post) {
        User user = currentUser();
        post.setUser_id(user.getId());
        postDAO.insertPost(post);
    }

    public void insertReplyPost(Post post, String post_uuid) {
        User user = currentUser();
        post.setUser_id(user.getId());
        postDAO.insertReplyPost(post, post_uuid);
    }

    public void updatePost(String uuid, String content) {
        postDAO.updatePost(uuid, content);
    }

    public void deletePost(String uuid) {
        postDAO.deletePost(uuid);
    }

    public Post showPost(String uuid) {
        return postDAO.showPost(uuid);
    }


    public ArrayList<Post> listPosts() {
        return postDAO.listPosts();
    }

    public ArrayList<Post> listReplyPosts(String uuid) {
        return postDAO.listReplyPosts(uuid);
    }

    public ArrayList<Post> listUserPosts(String uuid) {
        return postDAO.listUserPosts(uuid);
    }
}

