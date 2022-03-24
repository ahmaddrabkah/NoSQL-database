package com.atypon.BookStore.controllers;

import com.atypon.BookStore.models.User;
import com.atypon.BookStore.services.UserService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping( path ="/users" )
public class UserController {

    private final UserService userService = new UserService();
    @GetMapping(path = "/")
    public @ResponseBody List<User> getAllUsers(){
        return (List<User>) userService.readAllObjects();
    }

    @GetMapping(path = "/byUsername") public @ResponseBody User getUserByUsername(@RequestParam String username){
        List<User> users = (List<User>) userService.readAllObjects();
        for (User user:users) {
            if(username.equals(user.getUsername()))
                return user;
        }
        return null;
    }
}
