package com.imageProcessor.imageProcessor;

import com.imageProcessor.imageProcessor.userManagement.controller.UserController;
import com.imageProcessor.imageProcessor.userManagement.model.AppUser;
import com.imageProcessor.imageProcessor.userManagement.repository.UserRepo;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)

public class UserControllerTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    //initMocks not in use anymore
    //instantiates mocks and test classes
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    //userRepo methods do not have implementation, other than the one passed manually on thenReturn
    public void signupTestInvalidUsername() {

        String expected = "Username Already Exists!";

        List<AppUser> usersDb = Arrays.asList(
                new AppUser("John", "1234", "john@gmail.com")
        );


        Mockito.when(userRepo.findByUsername(Mockito.eq("John"))).thenReturn(usersDb);

        String actual = userController.signup( new AppUser("John", "dgkj", "john@hotmail.com"));

        Assert.assertEquals(expected, actual);

        Mockito.verify(userRepo).findByUsername(Mockito.eq("John"));
        Mockito.verifyNoMoreInteractions(userRepo);
    }

    @Test
    public void signupTestValidUsername() {

        String expected = "Signup Successful!";

        List<AppUser> usersDb = Arrays.asList();


        Mockito.when(userRepo.findByUsername(Mockito.eq("John"))).thenReturn(usersDb);

        String actual = userController.signup(new AppUser("John", "dgkj", "john@hotmail.com"));

        Assert.assertEquals(expected, actual);

        Mockito.verify(userRepo).save(Mockito.any(AppUser.class));

    }

    @Test
    public void deleteUser() {

        String expected = "User Deleted";

        Optional<AppUser> existingUser = Optional.of(new AppUser("Harry", "1234",
                "harry@email.com"));

        Mockito.when(userRepo.findOne(Mockito.any())).thenReturn(existingUser);

        String actual = userController.deleteUser("John", "dgkj");

        Assert.assertEquals(expected, actual);

    }


}
