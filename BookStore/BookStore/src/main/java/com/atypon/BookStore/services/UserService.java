package com.atypon.BookStore.services;

import com.atypon.BookStore.models.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserService extends Service{

    @Override
    public void addObject(JSONObject object) {
        databaseDriver.addObject("users",object);
    }

    @Override
    public void deleteObject(String id) {
        databaseDriver.deleteObject("users",id);
    }

    @Override
    public void updateObject(String id, JSONObject updatedObject) {
        databaseDriver.updateObject("users",id,updatedObject);
    }

    @Override
    public Object readObject(String id) {
        JSONObject object = databaseDriver.readObject("users",id);
        User user = new User();
        user.setId(object.get("id").toString());
        user.setUsername(object.get("username").toString());
        user.setPassword(object.get("password").toString());
        user.setRole(object.get("role").toString());
        return user;
    }

    @Override
    public Object readAllObjects() {
        List<JSONObject> objects = databaseDriver.readAllObject("users");
        List<User> users =new ArrayList<>();
        for (JSONObject object:objects) {
            User user = new User();
            user.setId(object.get("id").toString());
            user.setUsername(object.get("username").toString());
            user.setPassword(object.get("password").toString());
            user.setRole(object.get("role").toString());
            users.add(user);
        }
        return users;
    }
}
