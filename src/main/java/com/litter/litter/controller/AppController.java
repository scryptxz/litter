package com.litter.litter.controller;

import java.io.IOException;
import java.util.ArrayList;

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

        // Profile being viewed
        User user = us.showUser(handle);

        // Currently authenticated user
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = us.showUser(auth.getName());

        model.addAttribute("user", user);
        model.addAttribute("loggedUser", loggedUser);
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
    public String insertUser(
            @RequestParam("username") String username,
            @RequestParam("handle") String handle,
            @RequestParam("password") String password,
            @RequestParam(name = "picture", required = false) MultipartFile pictureFile) throws IOException {

        User user = new User();
        user.setUsername(username);
        user.setHandle(handle);
        user.setPassword(password);

        if (pictureFile != null && !pictureFile.isEmpty()) {

            String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
            String apiKey = System.getenv("CLOUDINARY_API_KEY");
            String apiSecret = System.getenv("CLOUDINARY_API_SECRET");

            if (cloudName == null || apiKey == null || apiSecret == null) {
                throw new IllegalStateException("Missing Cloudinary environment variables (CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET)");
            }

            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";

            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            String signatureBase = "public_id=";
            String publicId = null;
            String signature;
            try {
                signatureBase = "folder=users&timestamp=" + timestamp;
                javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(apiSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
                mac.init(secretKey);
                byte[] rawHmac = mac.doFinal(signatureBase.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                signature = java.util.Base64.getEncoder().encodeToString(rawHmac);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate Cloudinary signature", e);
            }

            String folder = "users";
            String secureUrl = com.litter.litter.util.CloudinaryUploadUtil.uploadImageToCloudinary(
                    pictureFile.getBytes(),
                    pictureFile.getOriginalFilename(),
                    cloudName,
                    apiKey,
                    apiSecret,
                    folder
            );

            user.setPicture(secureUrl);

        } else if (user.getPicture() == null) {
            user.setPicture("/img/default-icon.png");
        }


        UserService us = context.getBean(UserService.class);
        us.insertUser(user);
        return "redirect:/";

    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        return "signup";
    }

}
