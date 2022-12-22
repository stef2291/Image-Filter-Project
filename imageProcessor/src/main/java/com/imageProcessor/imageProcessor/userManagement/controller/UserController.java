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
import java.util.Random;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired
    UserRepo userRepo;

    //Saves new user in the database
    @PostMapping("/signup") //url:/signup?username={username}&passcode={password}&email={email}
    public String signup(@RequestBody AppUser appUser) {

        if (userRepo.findByUsername(appUser.getUsername()).isEmpty()) { //Tight coupling, Bad practice, fix it
            userRepo.save(new AppUser(appUser.getUsername(), appUser.getPassword(), appUser.getEmail()));
            return "Signup Successful!";

        } else {
            return "Username Already Exists!";
        }
    }

    //checks if username and password match the database, sends success or failure
    @PostMapping ("/login") //
    public Optional<AppUser> login(@RequestBody AppUser appUser) {

        Example<AppUser> loggedUser = Example.of(new AppUser(appUser.getUsername(), appUser.getPassword(), null));

        Optional<AppUser> actual = userRepo.findOne(loggedUser);

        if (actual.isPresent()) {

            String token = tokenGenerator();

            actual.get().setToken(token); // this is a temporary instance of AppUser, does not affect database user
            userRepo.save(actual.get());
            actual.get().setPassword("meow");

        }

        return actual;
    } //Credentials are never a param. Authentication body base64 encoded
    //when logged in successfully, receive a temporary code as authentication token
    //token user relation saved in database for reference from other APIs

    @PostMapping("/signout")
    public Boolean signout(@RequestBody AppUser user) {

        Optional<AppUser> userOptional = userRepo.findByToken(user.getToken());

        if(userOptional.isPresent()) {
            userOptional.get().setToken(null);
            userRepo.save(userOptional.get());
        }

        return userOptional.isPresent();
    }

    //"ebalwvwqjg" 2
    //"kauhhvyhyg" 3

    private String tokenGenerator() {

        int leftLimit = 97; // ascii letter a
        int rightLimit = 122; //ascii letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String token = random.ints(leftLimit, rightLimit+1)
                .limit(targetStringLength) //stream is not a list - check stream
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString(); //check StringBuilder (Java string concat is bad practice)

        return token;
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
