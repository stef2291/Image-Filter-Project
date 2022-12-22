package com.imageProcessor.imageProcessor.userManagement.controller;

import com.imageProcessor.imageProcessor.userManagement.repository.UserRepo;
import com.imageProcessor.imageProcessor.userManagement.model.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//Add Update Function
//Add logic
//View The Tables

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin

//Create a userService and put in the logic
//Post

public class UserController {

    @Autowired
    UserRepo userRepo;

    //Saves new user in the database
    @PostMapping("/signup") //url:/signup?username={username}&passcode={password}&email={email}
    public String signup(@RequestParam String username, @RequestParam String password, @RequestParam String email) {

        if (userRepo.findByUsername(username).isEmpty()) {

            System.out.println(username);
            System.out.println(password);
            System.out.println(email);

            userRepo.save(new AppUser(username, password, email));
            return "Signup Successful!";


        } else {
            return "Username Already Exists!";
        }

    }



    //checks if username and password match the database, sends success or failure
    @GetMapping("/login") //
    public Optional<AppUser> login(@RequestParam String username, @RequestParam String password) {

        Example<AppUser> loggedUser = Example.of(new AppUser(username, password, null));

        Optional<AppUser> actual = userRepo.findOne(loggedUser);

        return actual;
    }

    @DeleteMapping("/delete")
    @Transactional
    public String deleteUser(@RequestParam String username, @RequestParam String password) {

        Example<AppUser> loggedUser = Example.of(new AppUser(username, password, null));

        Optional<AppUser> actual = userRepo.findOne(loggedUser);

        if (actual.isPresent()) {
            userRepo.deleteById(actual.get().getId());
            return "User Deleted";
        } else {
            return "Username on Password Incorrect!";
        }
    }

    @PutMapping("/update")
    public String updateUser(@RequestParam String username, @RequestParam String password,
                             @RequestParam(required = false) String newEmail,
                             @RequestParam(required = false) String newPassword) {

        Example<AppUser> loggedUser = Example.of(new AppUser(username, password, null));
        Optional<AppUser> actual = userRepo.findOne(loggedUser);
        AppUser currentUser;


        if (actual.isPresent()) {
             currentUser = actual.get();
        } else {
            return "Username on Password Incorrect!";
        }

        if (newEmail != null) {
            currentUser.setEmail(newEmail);
            userRepo.save(currentUser);

            return "Email Successfully Updated";
        } else if (newPassword != null) {
            currentUser.setPassword(newPassword);
            userRepo.save(currentUser);

            return "Password Successfully Updated";
        }

        return "No new email on password found";
    }

}
