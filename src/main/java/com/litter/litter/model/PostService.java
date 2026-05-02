package com.litter.litter.model;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    PostDAO postDAO;

    public void insertPost(Post post) {
        postDAO.insertPost(post);
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

    public ArrayList<Post> listUserPosts(String uuid) {
        return postDAO.listUserPosts(uuid);
    }
}
