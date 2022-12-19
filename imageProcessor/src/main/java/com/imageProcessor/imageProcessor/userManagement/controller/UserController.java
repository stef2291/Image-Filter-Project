package com.imageProcessor.imageProcessor.userManagement.controller;

import com.imageProcessor.imageProcessor.userManagement.repository.UserRepo;
import com.imageProcessor.imageProcessor.userManagement.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController  {

    @Autowired
    UserRepo userRepo;
    //Saves new user in the database
    @PostMapping("/signup") //url:/signup?username={username}&passcode={password}&email={email}
    public AppUser signup(@RequestParam String username, @RequestParam String password, @RequestParam String email) {

        AppUser newUser = userRepo.save(new AppUser(username, password, email ));

        return newUser;
    }

    //checks if username and password match the database, sends success or failure
    @GetMapping("/login") //
    public String login(@RequestParam String username, @RequestParam String password) {

        userRepo.findByUsername(username);

        return "";
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String username, String password) {

        userRepo.deleteByUsername(username);

        return "mainPage";
    }

}
