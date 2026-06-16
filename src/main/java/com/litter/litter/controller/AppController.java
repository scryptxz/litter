package com.litter.litter.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.litter.litter.model.Post;
import com.litter.litter.model.PostService;
import com.litter.litter.model.User;
import com.litter.litter.model.UserService;

import jakarta.servlet.http.HttpServletRequest;

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
    public String insertPost(@ModelAttribute Post post) {
        PostService cs = context.getBean(PostService.class);
        cs.insertPost(post);
        return "redirect:/";
    }

    @PostMapping("/reply_post/{post_uuid}")
    public String insertReplyPost(@ModelAttribute Post post, @PathVariable String post_uuid) {
        PostService cs = context.getBean(PostService.class);
        cs.insertReplyPost(post, post_uuid);
        return "redirect:/post/" + post_uuid;
    }

    @GetMapping("/")
    public String listPosts(Model model) {
        PostService cs = context.getBean(PostService.class);
        ArrayList<Post> posts = (ArrayList<Post>) cs.listPosts();
        UserService us = context.getBean(UserService.class);
        // With Spring Security configured, authentication name is USERS.HANDLE
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user = us.showUser(auth.getName());
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("user", user);
        return "index";
    }


    @GetMapping("/delete/{uuid}")
    public String deletePost(@PathVariable String uuid, Model model, HttpServletRequest request) {
        PostService cs = context.getBean(PostService.class);
        cs.deletePost(uuid);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/post/{uuid}")
    public String showPost(@PathVariable String uuid, Model model) {
        PostService cs = context.getBean(PostService.class);
        Post post = cs.showPost(uuid);
        UserService us = context.getBean(UserService.class);
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user = us.showUser(auth.getName());
        ArrayList<Post> posts = (ArrayList<Post>) cs.listReplyPosts(uuid);
        model.addAttribute("reply", new Post());
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "post";
    }


    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String insertUser(@ModelAttribute User user, Model model) {
        UserService us = context.getBean(UserService.class);
        us.insertUser(user);
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        model.addAttribute("image", base64Image);

        return "signup";
    }

}
