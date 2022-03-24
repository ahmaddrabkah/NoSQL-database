package com.atypon.BookStore.controllers;

import com.atypon.BookStore.models.Book;
import com.atypon.BookStore.services.BookService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping( path ="/books" )
public class BookController {
    private final BookService bookService = new BookService();

    @GetMapping(path = "/")
    public @ResponseBody List<Book> getAllBooks(){
        return (List<Book>)bookService.readAllObjects();
    }
    @GetMapping(path = "/byID")
    public @ResponseBody Book getBookByID(@RequestParam String id){
        return (Book)bookService.readObject(id);
    }
    @PostMapping
    public void addBook(@RequestBody JSONObject book){
        bookService.addObject(book);
    }
    @PutMapping
    public void updateBook(@RequestBody JSONObject book){
        String id = book.get("id").toString();
        bookService.updateObject(id,book);
    }
    @DeleteMapping
    public void deleteBook(@RequestParam String id){
        bookService.deleteObject(id);
    }
}
