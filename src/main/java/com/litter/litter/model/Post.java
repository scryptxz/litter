package com.litter.litter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Post {

    private String content;
    private String uuid;
    private String user_id;
    private String created_at;
    private String username;
    private String handle;
    private String picture;

    public Post() {
    }

    ;

    public Post(String content, String uuid, String user_id, String created_at, String username, String handle, String picture) {
        this.content = content;
        this.uuid = uuid;
        this.user_id = user_id;
        this.created_at = created_at;
        this.username = username;
        this.handle = handle;
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUsername() {
        return username;
    }

    public String getHandle() {
        return handle;
    }

    public static Post convert(Map<String, Object> register) {
        String content = (String) register.get("content");
        String uuid = register.get("id") != null ? register.get("id").toString() : null;
        String user_id = register.get("user_id") != null ? register.get("user_id").toString() : null;
        String created_at = register.get("created_at") != null ? register.get("created_at").toString() : null;
        String username = register.get("username") != null ? register.get("username").toString() : null;
        String handle = register.get("handle") != null ? register.get("handle").toString() : null;
        String picture = register.get("picture") != null ? register.get("picture").toString() : null;
        return new Post(content, uuid, user_id, created_at, username, handle, picture);
    }

    public static ArrayList<Post> convertAll(List<Map<String, Object>> registers) {
        ArrayList<Post> aux = new ArrayList<>();
        for (Map<String, Object> register : registers) {
            aux.add(convert(register));
        }
        return aux;
    }

    public String getPicture() {
        return picture;
    }


}
