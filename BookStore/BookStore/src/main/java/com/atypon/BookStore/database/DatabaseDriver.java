package com.atypon.BookStore.database;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class DatabaseDriver {
    private final String username;
    private final String password;
    private final String schemaName;
    private Socket socket;
    private ObjectInputStream inputFromServer;
    private ObjectOutputStream outputToServer;
    private static DatabaseDriver databaseDriver = null;

    public static DatabaseDriver getInstance(String username, String password,String schemaName){
        if(databaseDriver == null){
            synchronized (DatabaseDriver.class){
                databaseDriver = new DatabaseDriver(username,password,schemaName);
            }
        }
        return databaseDriver;
    }
    private DatabaseDriver(String username, String password,String schemaName){
        this.username = username;
        this.password = password;
        this.schemaName = schemaName;
        int nodePortNumber = getNodePortNumberFromMaster();
        connect(nodePortNumber);
    }
    private void connect(int portNumber){
        try {
            socket = new Socket("localhost",portNumber);
            outputToServer = new ObjectOutputStream(socket.getOutputStream());
            inputFromServer = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int getNodePortNumberFromMaster(){
        int nodePortNumber = -1;
        try {
            socket = new Socket("localhost",8100);
            outputToServer = new ObjectOutputStream(socket.getOutputStream());
            inputFromServer = new ObjectInputStream(socket.getInputStream());
            outputToServer.writeObject("client");
            outputToServer.writeObject(username);
            outputToServer.writeObject(password);
            if((boolean)inputFromServer.readObject())
                nodePortNumber = (int) inputFromServer.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nodePortNumber;
    }
    public void addObject(String typeName, JSONObject object){
        try {
            outputToServer.writeObject("3");
            outputToServer.writeObject(schemaName);
            outputToServer.writeObject(typeName);
            for (Object key: object.keySet()) {
                outputToServer.writeObject(object.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteObject(String typeName,String id){
        try {
            outputToServer.writeObject("4");
            outputToServer.writeObject(schemaName);
            outputToServer.writeObject(typeName);
            outputToServer.writeObject(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateObject(String typeName,String id,JSONObject object){
        try {
            outputToServer.writeObject("5");
            outputToServer.writeObject(schemaName);
            outputToServer.writeObject(typeName);
            outputToServer.writeObject(id);
            outputToServer.writeObject(object.get("key"));
            outputToServer.writeObject(object.get("value"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public JSONObject readObject(String typeName, String id){
        JSONObject object = null;
        try {
            outputToServer.writeObject("6");
            outputToServer.writeObject(schemaName);
            outputToServer.writeObject(typeName);
            outputToServer.writeObject(id);
            object = readJSONObject();;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
    private JSONObject readJSONObject() throws IOException, ClassNotFoundException {
        String input = null;
        do {
            input = inputFromServer.readObject().toString();
        } while (!input.contains("{"));
        JSONObject object = null;
        try {
            object = (JSONObject) new JSONParser().parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return object;
    }
    public List<JSONObject> readAllObject(String typeName){
        List<JSONObject> objects = new ArrayList<>();
        try {
            outputToServer.writeObject("7");
            outputToServer.writeObject(schemaName);
            outputToServer.writeObject(typeName);
            objects = readListOfJSONObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }
    private List<JSONObject> readListOfJSONObjects(){
        List<JSONObject> objects = new ArrayList<>();
        while(true){
            try {
                HashMap<String, JSONObject> allObjects =(HashMap<String, JSONObject>) inputFromServer.readObject();
                objects.addAll(allObjects.values());
                break;
            }catch (ClassCastException | IOException | ClassNotFoundException e) {
                if(e instanceof ClassNotFoundException || e instanceof  IOException)
                    e.printStackTrace();
            }
        }
        return objects;
    }
}