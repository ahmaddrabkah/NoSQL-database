package com.atypon.BookStore.services;

import com.atypon.BookStore.database.DatabaseDriver;
import org.json.simple.JSONObject;

import java.util.List;

public abstract class Service {
    protected final DatabaseDriver databaseDriver
            = DatabaseDriver.getInstance("admin","1234","bookstore");
    public abstract void addObject(JSONObject object);
    public abstract void deleteObject(String id);
    public abstract void updateObject(String id, JSONObject updatedObject);
    public abstract Object readObject(String id);
    public abstract Object readAllObjects();
}
