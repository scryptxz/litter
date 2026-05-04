/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.litter.litter.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Renan
 */
@Service
public class UserService {

    @Autowired
    UserDAO userDAO;


    public User showUser(String handle) {
        return userDAO.showUser(handle);
    }
}
