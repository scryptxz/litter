/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.litter.litter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Renan
 */
public class User {

    private String id;
    private String handle;
    private String username;
    private String password;
    private String picture;
    private String created_at;

    public User() {
    }

    ;

    public User(String id, String handle, String username, String created_at, String picture, String password) {
        this.created_at = created_at;
        this.handle = handle;
        this.id = id;
        this.password = password;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public String getPassword() {
        return password;
    }

    public String getPicture() {
        return picture;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUsername() {
        return username;
    }

    public static User convert(Map<String, Object> register) {
        String id = register.get("id") != null ? register.get("id").toString() : null;
        String handle = register.get("handle") != null ? register.get("handle").toString() : null;
        String username = register.get("username") != null ? register.get("username").toString() : null;
        String created_at = register.get("created_at") != null ? register.get("created_at").toString() : null;
        String picture = register.get("picture") != null ? register.get("picture").toString() : null;
        String password = register.get("password") != null ? register.get("password").toString() : null;
        return new User(id, handle, username, created_at, picture, password);
    }

    public static ArrayList<User> convertAll(List<Map<String, Object>> registers) {
        ArrayList<User> aux = new ArrayList<>();
        for (Map<String, Object> register : registers) {
            aux.add(convert(register));
        }
        return aux;
    }

}
