package com.imageProcessor.imageProcessor.userManagement.repository;

import com.imageProcessor.imageProcessor.userManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

//    public String createUser (String username, String password, String email);

}
