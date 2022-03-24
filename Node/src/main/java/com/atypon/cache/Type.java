package com.atypon.cache;

import com.atypon.FileReader;
import org.json.simple.JSONObject;

import java.util.*;

public class Type {
    private final Map<String, JSONObject> objects = new HashMap<>();
    private final Deque<String> LRUObject = new LinkedList<>();
    private final String SCHEMA_NAME;
    private final String TYPE_NAME;
    private final int MAX_SIZE;
    public Type(String schemaName, String typeName, int maxSize) {
        this.SCHEMA_NAME = schemaName;
        this.TYPE_NAME = typeName;
        this.MAX_SIZE = maxSize;
    }
    public JSONObject getObject(String id){
        synchronized (objects) {
            synchronized (LRUObject) {
                if(objects.containsKey(id)) {
                    moveObjectToFirst(id);
                    return objects.get(id);
                }
                JSONObject object = getObjectIfExist(id);
                if(object != null){
                    addObject(id,object);
                    return object;
                }
            }
        }
        return null;
    }
    private void moveObjectToFirst(String id){
        synchronized (LRUObject) {
            LRUObject.remove();
            LRUObject.addFirst(id);
        }
    }
    private JSONObject getObjectIfExist(String id){
        FileReader dataReader = FileReader.getInstance("/database");
        Map<String, JSONObject> allObjects =  dataReader.getAllFilesDataInDirectory(SCHEMA_NAME +"/"+ TYPE_NAME);
        if(allObjects.containsKey(id)) {
            return allObjects.get(id);
        }
        return null;
    }
    public void addObject(String id, JSONObject object){
        synchronized (objects){
            synchronized (LRUObject){
                if( LRUObject.size() == MAX_SIZE){
                    String leastUsedObject = LRUObject.removeLast();
                    objects.remove(leastUsedObject);
                }
                LRUObject.addFirst(id);
                objects.put(id,object);
            }
        }
    }
}
