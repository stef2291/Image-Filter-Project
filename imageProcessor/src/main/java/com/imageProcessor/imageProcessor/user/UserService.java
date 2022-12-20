package com.imageProcessor.imageProcessor.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User createUser(User user){
        System.out.println("trying to create a user");
        Optional<User> isEmailUnique = userRepository.findByEmail(user.getEmail());
        Optional<User> isUsernameUnique = userRepository.findByUsername(user.getUsername());

        System.out.println(isEmailUnique.isPresent());
        System.out.println(isUsernameUnique.isPresent());

        if(isEmailUnique.isPresent() == true || isUsernameUnique.isPresent() == true ){
            //in this case, one or the other is already taken, so throw an exception
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Email or username already exists"); //this is a 400 status
        }

        return userRepository.save(user);
    }

    public User logIn(String email, String password){
        //find by email
        Optional<User> matchedUser = userRepository.findByEmail(email);

        //firstly. check user actually exists
        if(matchedUser.isPresent() == false){
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

        //compare password
        //if password matches, update user to loggedIn
        if(matchedUser.get().getPassword().equals(password)){
            if(matchedUser.get().getIsLoggedIn() == true){
                //user already logged out, send 406 status
                throw new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, "User already logged out");
            }

            matchedUser.get().setIsLoggedIn(true);
            userRepository.logInUser(true, matchedUser.get().getId()); //update in DB
        } else{
            //password incorrect, send 401 status
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Password incorrect");
        }
        //return logged in user
        return matchedUser.get();
    }

    public User logOut(String email, String password){
        //find by email
        Optional<User> matchedUser = userRepository.findByEmail(email);

        //firstly. check user actually exists
        if(matchedUser.isPresent() == false){
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

        //compare password
        //if password matches, update user to loggedIn
        if(matchedUser.get().getPassword().equals(password)){
            if(matchedUser.get().getIsLoggedIn() == false){
                //user already logged out, send 406 status
                throw new ResponseStatusException( HttpStatus.NOT_ACCEPTABLE, "User already logged out");
            }

            matchedUser.get().setIsLoggedIn(false);
            userRepository.logInUser(false, matchedUser.get().getId()); //update in DB
        } else{
            //password incorrect, send 401 status
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Password incorrect");
        }
        //return logged in user
        return matchedUser.get();
    }

    public boolean deleteUser(String email, String username, String password){
        //find by email
        Optional<User> matchedUser = userRepository.findByEmail(email);

        //firstly. check user actually exists
        if(matchedUser.isPresent() == false){
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

        //check username matches, check password matches
        //delete use if so
        if(matchedUser.get().getPassword().equals(password) && matchedUser.get().getUsername().equals(username)){
            userRepository.deleteById(matchedUser.get().getId());
            return true;
        }else{
            return false;
        }
    }

    public Optional<User> findById(Long Id){
        return userRepository.findById(Id);
    }
}
