package com.imageProcessor.imageProcessor.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    //analogous to user router in the OG project
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getUsers(){
        return userService.getAllUsers();

    }

    @PostMapping("/")
    public User createUser(@Valid @RequestBody User user){
        userService.createUser(user);
        return user;
    }

    @PostMapping("/login")
    public User logInUser(@RequestBody Map<String, String> user){
        User loggedInUser = userService.logIn(user.get("email"), user.get("password"));

        return loggedInUser; //this will need to be changed once we've done our DB update
    }

    @PostMapping("/logout")
    public User logOutUser(@RequestBody Map<String, String> user){
        User loggedInUser = userService.logOut(user.get("email"), user.get("password"));

        return loggedInUser; //this will need to be changed once we've done our DB update
    }

    @DeleteMapping("/")
    public Boolean deleteUser(@RequestBody Map<String, String> user){
        Boolean success = userService.deleteUser(user.get("email"), user.get("username"), user.get("password"));
        return success;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
