package com.litter.litter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.litter.litter.model.PostService;
import com.litter.litter.model.UserService;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/update")
    public String updatePost(
            @RequestParam("uuid") String uuid,
            @RequestParam("content") String content) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String handle = auth.getName();

        var currentUser = userService.showUser(handle);
        var post = postService.showPost(uuid);
        if (post != null && post.getUser_id() != null && post.getUser_id().equals(currentUser.getId())) {
            postService.updatePost(uuid, content);
        }

        return "redirect:/";
    }
}

