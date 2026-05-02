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



@Controller
public class AppController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/user/{uuid}")
    public String listUserPosts(@PathVariable String uuid, Model model) {
        PostService cs = context.getBean(PostService.class);
        ArrayList<Post> posts = (ArrayList<Post>) cs.listUserPosts(uuid);
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
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        return "index";
    }

    @GetMapping("/delete/{uuid}")
    public String deletePost(@PathVariable String uuid, Model model) {
        PostService cs = context.getBean(PostService.class);
        cs.deletePost(uuid);
        return "redirect:/";
    }
    
    
}
