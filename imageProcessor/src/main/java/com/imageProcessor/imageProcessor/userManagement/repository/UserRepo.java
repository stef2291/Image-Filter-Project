package com.imageProcessor.imageProcessor.userManagement.repository;

import com.imageProcessor.imageProcessor.userManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);
    List<User> deleteByUsername(String username);
//    public String createUser (String username, String password, String email);

}
