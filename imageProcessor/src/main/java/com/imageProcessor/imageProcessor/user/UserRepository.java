package com.imageProcessor.imageProcessor.user;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    //custom queries and methods go here
    public Optional<User> findByEmail(String email);
    public Optional<User> findByUsername(String username);
    public Optional<User> findById(Long id);

    //This method will set specified user to logged in (hopefully!!)
    @Transactional //Not sure why this is required, but the query will not work otherwise
    @Modifying //Specifies this query method modifies an entry
    @Query("update User u set u.isLoggedIn = ?1 where u.id = ?2 ") //this annotation is more or less a written SQL query , u.age = ?4
    int logInUser(Boolean isLoggedIn, Long id);
}
