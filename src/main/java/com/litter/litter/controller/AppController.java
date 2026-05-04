package com.litter.litter.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.litter.litter.model.Post;
import com.litter.litter.model.PostService;
import com.litter.litter.model.User;
import com.litter.litter.model.UserService;



@Controller
public class AppController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/{handle}")
    public String listUserPosts(@PathVariable String handle, Model model) {
        PostService cs = context.getBean(PostService.class);
        ArrayList<Post> posts = (ArrayList<Post>) cs.listUserPosts(handle);
        UserService us = context.getBean(UserService.class);
        User user = us.showUser(handle);
        model.addAttribute("user", user);
        model.addAttribute("user_posts", posts);
        model.addAttribute("user_post", new Post());
        return "user";
    }

    @PostMapping("/post")
    public String insertPost(@ModelAttribute Post post, Model model) {
        PostService cs = context.getBean(PostService.class);
        cs.insertPost(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String listPosts(Model model) {
        PostService cs = context.getBean(PostService.class);
        ArrayList<Post> posts = (ArrayList<Post>) cs.listPosts();
        UserService us = context.getBean(UserService.class);
        User user = us.showUser("jun3301");
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/delete/{uuid}")
    public String deletePost(@PathVariable String uuid, Model model) {
        PostService cs = context.getBean(PostService.class);
        cs.deletePost(uuid);
        return "redirect:/";
    }

    @GetMapping("/post/{uuid}")
    public String showPost(@PathVariable String uuid, Model model) {
        PostService cs = context.getBean(PostService.class);
        Post post = cs.showPost(uuid);
        model.addAttribute("post", post);
        return "post";
    }
    

}
