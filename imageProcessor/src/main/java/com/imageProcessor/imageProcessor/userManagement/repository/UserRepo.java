package com.imageProcessor.imageProcessor.userManagement.repository;

import com.imageProcessor.imageProcessor.userManagement.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<AppUser, Long> {

    List<AppUser> findByUsername(String username);
    List<AppUser> deleteByUsername(String username);
//    public String createUser (String username, String password, String email);

}
