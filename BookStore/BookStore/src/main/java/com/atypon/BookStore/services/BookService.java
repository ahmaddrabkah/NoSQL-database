package com.atypon.BookStore.services;

import com.atypon.BookStore.models.Book;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookService extends Service{

    @Override
    public void addObject(JSONObject object) {
        databaseDriver.addObject("books",object);
    }

    @Override
    public void deleteObject(String id) {
        databaseDriver.deleteObject("books",id);
    }

    @Override
    public void updateObject(String id, JSONObject updatedObject) {
        databaseDriver.updateObject("books",id,updatedObject);
    }

    @Override
    public Object readObject(String id) {
        JSONObject object = databaseDriver.readObject("books",id);
        Book book = new Book();
        book.setId(object.get("id").toString());
        book.setName(object.get("name").toString());
        book.setSubject(object.get("subject").toString());
        book.setDescription(object.get("description").toString());
        return book;
    }

    @Override
    public Object readAllObjects(){
        List<JSONObject> objects= databaseDriver.readAllObject("books");
        List<Book> books = new ArrayList<>();
        for (JSONObject object : objects) {
            Book book = new Book();
            book.setId(object.get("id").toString());
            book.setName(object.get("name").toString());
            book.setSubject(object.get("subject").toString());
            book.setDescription(object.get("description").toString());
            books.add(book);
        }
        return books;
    }
}
