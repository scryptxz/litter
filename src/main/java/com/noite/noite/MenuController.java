package com.noite.noite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/")
    public String paginaPrincipal() {
        return "index";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }
}
