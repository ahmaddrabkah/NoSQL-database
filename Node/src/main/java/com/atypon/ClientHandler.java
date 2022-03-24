package com.atypon;

import com.atypon.cache.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.util.HashMap;

public class ClientHandler {
    private final ObjectInputStream inputFromUser;
    private final ObjectOutputStream outputToUser;
    private final Cache cache = LRUCache.getInstance();
    private final FileReader dataReader = FileReader.getInstance("/database");

    public ClientHandler(ObjectInputStream inputFromUser, ObjectOutputStream outputToUser) {
        this.inputFromUser = inputFromUser;
        this.outputToUser = outputToUser;
    }
    public Object[] handleClientOperation(String operation) throws IOException, ClassNotFoundException {
        Object[] objects = new Object[2];
        if(operation.equals("1")){
            objects[0] = "create";
            objects[1] = createSchema();
        }
        else if(operation.equals("2"))
        {
            objects[0] = "addType";
            objects[1] = addObjectType();
        }
        else if(operation.equals("3")){
            objects[0] = "addObject";
            objects[1] = addObject();
        }
        else if(operation.equals("4")){
            objects[0] = "deleteObject";
            objects[1] = deleteObject();
        }
        else if(operation.equals("5")){
            objects[0] = "updateObject";
            objects[1] = updateObject();
        }
        else if(operation.equals("6")) {
            readObject();
            objects = null;
        }
        else if(operation.equals("7")) {
            readAllObjects();
            objects = null;
        }
        return objects;
    }
    private Object createSchema() throws IOException, ClassNotFoundException {
        HashMap<String, String> schemaInfo = readSchemaInfoFromUser();
        return schemaInfo;
    }
    private Object addObjectType() throws IOException, ClassNotFoundException {
        String schemaName = getValidSchemaNameFromUser();
        String objectTypeName = readFromUser("Enter type name : ");
        JSONObject object = readTypeFromUser();
        HashMap<String, String> typeInfo = new HashMap<>();
        typeInfo.put("schemaName",schemaName);
        typeInfo.put(objectTypeName,object.toJSONString());
        return typeInfo;
    }
    private Object addObject() throws IOException, ClassNotFoundException {
        String schemaName = getValidSchemaNameFromUser();
        String typeName = getValidTypeNameFromUser(schemaName);
        Schema schema = (Schema)cache.getBlock(schemaName);
        JSONObject objectStructure = schema.getType(typeName).getObject(typeName+"Structure");
        JSONObject object = readObjectValuesFromUser(objectStructure);
        HashMap<String, String> objectInfo = new HashMap<>();
        objectInfo.put("schemaName",schemaName);
        objectInfo.put("typeName",typeName);
        objectInfo.put("object",object.toJSONString());
        return objectInfo;
    }
    private JSONObject readObjectValuesFromUser(JSONObject objectStructure) throws IOException, ClassNotFoundException {
        JSONObject object = new JSONObject();
        for (Object key:objectStructure.keySet()) {
            String value = readFromUser("Enter value of "+key+" : ");
            object.put(key,value);
        }
        return object;
    }
    private Object deleteObject() throws IOException, ClassNotFoundException {
        HashMap<String, String> objectInfo = readObjectInfoFromUser();
        return objectInfo;
    }
    private Object updateObject() throws IOException, ClassNotFoundException {
        HashMap<String, String> objectInfo = readObjectInfoFromUser();
        if(objectInfo != null) {
            String schemaName = objectInfo.get("schemaName");
            String typeName = objectInfo.get("typeName");
            String id = objectInfo.get("id");
            Schema schema = (Schema)cache.getBlock(schemaName);
            JSONObject object = schema.getType(typeName).getObject(id);
            JSONObject newObject = readValueOfObjectKeyFromUser(object);
            objectInfo.put("object",newObject.toJSONString());
        }
        return objectInfo;
    }
    private JSONObject readValueOfObjectKeyFromUser(JSONObject object) throws IOException, ClassNotFoundException {
        JSONObject newObject = null;
        try {
            String oldObject = object.toJSONString();
            newObject = (JSONObject) new JSONParser().parse(oldObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outputToUser.writeObject("Select key you want to update : "+newObject.keySet()+"\n");
        String key = readFromUser("Enter key : ").toLowerCase();
        while (! newObject.containsKey(key)){
            outputToUser.writeObject("Invalid key, please try again");
            key = readFromUser("Enter key : ").toLowerCase();
        }
        String value = readFromUser("Enter value : ");
        newObject.remove(key);
        newObject.put(key,value);
        return newObject;
    }
    private void readObject() throws IOException, ClassNotFoundException {
        HashMap<String, String> objectInfo = readObjectInfoFromUser();
        if(objectInfo != null) {
            String schemaName = objectInfo.get("schemaName");
            String typeName = objectInfo.get("typeName");
            String id = objectInfo.get("id");
            Schema schema = (Schema)cache.getBlock(schemaName);
            JSONObject object = schema.getType(typeName).getObject(id);
            outputToUser.writeObject(object.toJSONString());
        }
    }
    private void readAllObjects() throws IOException, ClassNotFoundException {
        String schemaName = getValidSchemaNameFromUser();
        String typeName = getValidTypeNameFromUser(schemaName);
        HashMap<String, JSONObject> allObjects = dataReader.getAllFilesDataInDirectory(schemaName+"/"+typeName);
        outputToUser.writeObject(allObjects);
    }
    private HashMap<String, String> readObjectInfoFromUser() throws IOException, ClassNotFoundException {
        String schemaName = getValidSchemaNameFromUser();
        String typeName = getValidTypeNameFromUser(schemaName);
        String id = readFromUser("Enter object ID :");
        if(isValidID(schemaName,typeName,id)){
            HashMap<String, String> data = new HashMap<>();
            data.put("schemaName",schemaName);
            data.put("typeName",typeName);
            data.put("id",id);
            return data;
        }
        else {
            outputToUser.writeObject("No such ID");
            return null;
        }
    }
    private boolean isValidID(String schemaName,String typeName,String id){
        String path = schemaName+"/"+typeName;
        return dataReader.isFileExist(path,id+".txt");
    }
    private String getValidSchemaNameFromUser() throws IOException, ClassNotFoundException {
        String schemaName = readFromUser("Enter schema name : ").toLowerCase();
        while(! isValidSchemaName(schemaName)){
            outputToUser.writeObject("Invalid schema name, please try again\n");
            schemaName = readFromUser("Enter schema name : ").toLowerCase();
        }
        return schemaName;
    }
    private boolean isValidSchemaName(String schemaName){
        return dataReader.isFileExist(null,schemaName);
    }
    private String getValidTypeNameFromUser(String schemaName)throws IOException, ClassNotFoundException {
        String typeName = readFromUser("Enter type name : ").toLowerCase();
        while(! isValidTypeName(schemaName,typeName)){
            outputToUser.writeObject("Invalid type name, please try again\n");
            typeName = readFromUser("Enter type name : ").toLowerCase();
        }
        return typeName;
    }
    private boolean isValidTypeName(String schemaName,String typeName){
        String path = schemaName;
        return dataReader.isFileExist(path,typeName);
    }
    private HashMap<String, String> readSchemaInfoFromUser() throws IOException, ClassNotFoundException {
        String schemaName = readFromUser("Enter schema name : ").toLowerCase();
        String objectTypeName = readFromUser("Enter type name : ").toLowerCase();
        JSONObject object = readTypeFromUser();

        HashMap<String, String> schemaInfo = new HashMap<>();
        schemaInfo.put("schemaName",schemaName);
        schemaInfo.put(objectTypeName,object.toJSONString());

        String input;
        while (true){
            input = readFromUser("Do you want to enter another type [Y/N] :");
            if(input.equalsIgnoreCase("N"))
                break;
            objectTypeName = readFromUser("Enter type name : ").toLowerCase();
            object = readTypeFromUser();
            schemaInfo.put(objectTypeName,object.toJSONString());
        }
        //outputToUser.writeObject("Done");
        return schemaInfo;
    }
    private JSONObject readTypeFromUser() throws IOException, ClassNotFoundException{
        outputToUser.writeObject("Set Object keys (End for exit) :\n");
        JSONObject object = new JSONObject();
        String input;
        while (true){
            input = readFromUser("Enter key : ").toLowerCase();
            if(input.equalsIgnoreCase("end"))
                break;
            if(input.equals(""))
                continue;
            object.put(input,"");
        }
        if (!String.valueOf(object).contains("id")){
            object.put("id","");
        }
        return object;
    }
    private String readFromUser(String message) throws IOException, ClassNotFoundException {
        outputToUser.writeObject(message);
        String input = (String)inputFromUser.readObject();
        return input;
    }
}
