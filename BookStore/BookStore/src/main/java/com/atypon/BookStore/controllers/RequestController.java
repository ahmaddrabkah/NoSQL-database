package com.atypon.BookStore.controllers;

import com.atypon.BookStore.models.Request;
import com.atypon.BookStore.services.RequestService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping( path ="/requests" )
public class RequestController {
    private final RequestService requestService = new RequestService();

    @GetMapping(path = "/")
    public @ResponseBody List<Request> getAllRequests(){
        return (List<Request>)requestService.readAllObjects();
    }
    @GetMapping(path = "/byID")
    public @ResponseBody Request getRequestByID(@RequestParam String id){
        return (Request)requestService.readObject(id);
    }
    @PostMapping
    public void addRequest(@RequestBody JSONObject request){
        requestService.addObject(request);
    }
    @PutMapping
    public void updateRequest(@RequestBody JSONObject request){
        String id = request.get("id").toString();
        requestService.updateObject(id,request);
    }
    @DeleteMapping
    public void deleteRequest(@RequestParam String id){
        requestService.deleteObject(id);
    }
}
