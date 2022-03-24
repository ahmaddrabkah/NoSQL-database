package com.atypon.BookStore.services;

import com.atypon.BookStore.models.Request;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestService extends Service{
    @Override
    public void addObject(JSONObject object) {
        databaseDriver.addObject("requests",object);
    }

    @Override
    public void deleteObject(String id) {
        databaseDriver.deleteObject("requests",id);
    }

    @Override
    public void updateObject(String id, JSONObject updatedObject) {
        databaseDriver.updateObject("requests",id,updatedObject);
    }

    @Override
    public Object readObject(String id) {
        JSONObject object = databaseDriver.readObject("requests",id);
        Request request = new Request();
        request.setId(object.get("id").toString());
        request.setUserID(object.get("user_id").toString());
        request.setUserID(object.get("book_id").toString());
        request.setLocation(object.get("location").toString());
        return request;
    }

    @Override
    public Object readAllObjects() {
        List<JSONObject> objects = databaseDriver.readAllObject("requests");
        List<Request> requests = new ArrayList<>();
        for (JSONObject object:objects) {
            Request request = new Request();
            request.setId(object.get("id").toString());
            request.setUserID(object.get("user_id").toString());
            request.setUserID(object.get("book_id").toString());
            request.setLocation(object.get("location").toString());
            requests.add(request);
        }
        return requests;
    }
}
