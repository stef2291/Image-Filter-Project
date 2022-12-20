package com.imageProcessor.imageProcessor;

import com.imageProcessor.imageProcessor.user.User;
import com.imageProcessor.imageProcessor.user.UserController;
import com.imageProcessor.imageProcessor.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Test
    public void testGetUsers() {
        // Set up test data
        User user1 = new User("user1", "password1", "user1@example.com");
        User user2 = new User("user2", "password2", "user2@example.com");
        userService.createUser(user1);
        userService.createUser(user2);

        // Call the getUsers method
        List<User> users = userController.getUsers();

        // Verify the results
        assertEquals(2, users.size());
    }

    @Test
    public void testCreateUser() {
        // Set up test data
        User user = new User("user1", "password1", "user1@example.com");

        // Call the createUser method
        User createdUser = userController.createUser(user);

        // Verify the results
        assertEquals(user, createdUser);
        Optional<User> foundUser = userService.findById(createdUser.getId());
        assertTrue(foundUser.isPresent());
    }

    @Test
    public void testLogInUser() {
        // Set up test data
        User user = new User("user1", "password1", "user1@example.com");
        userService.createUser(user);
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "user1@example.com");
        loginData.put("password", "password1");

        // Call the logInUser method
        User loggedInUser = userController.logInUser(loginData);

        // Verify the results
        assertTrue(loggedInUser.getIsLoggedIn());
        Optional<User> foundUser = userService.findById(loggedInUser.getId());
        assertTrue(foundUser.isPresent());
        assertTrue(foundUser.get().getIsLoggedIn());
    }

    @Test
    public void testLogOutUser() {
        // Set up test data
        User user = new User("user1", "password1", "user1@example.com");
        user.setIsLoggedIn(true);
        userService.createUser(user);
        Map<String, Long> logoutData = new HashMap<>();
        logoutData.put("userId", user.getId());

        // Call the logOutUser method
        Boolean success = userController.logOutUser(logoutData);

        // Verify the results
        assertTrue(success);
        Optional<User> foundUser = userService.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertFalse(foundUser.get().getIsLoggedIn());
    }

    @Test
    public void testDeleteUser() {
        // Set up test data
        User user = new User("user1", "password1", "user1@example.com");
        userService.createUser(user);
        Map<String, String> deleteData = new HashMap<>();
        deleteData.put("email", "user1@example.com");
        deleteData.put("username", "user1");
        deleteData.put("password", "password1");

        // Call the deleteUser method
        Boolean success = userController.deleteUser(deleteData);

        // Verify the results
        assertTrue(success);
        Optional<User> foundUser = userService.findById(user.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testHandleValidationExceptions() {
        // Set up test data
        User user = new User("", "password1", "user1@example.com");

        // Create a MethodArgumentNotValidException
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, null);

        // Call the handleValidationExceptions method
        Map<String, String> errors = userController.handleValidationExceptions(exception);

        // Verify the results
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("username"));
        assertEquals("must not be empty", errors.get("username"));
    }
}


