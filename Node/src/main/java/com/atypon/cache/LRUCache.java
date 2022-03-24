package com.atypon.cache;

import com.atypon.*;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

public class LRUCache implements Cache{
    private final int MAX_SCHEMA_NUMBER = 2;
    private final int MAX_Object_NUMBER = 2;
    private boolean isDirty = false;
    private final String PATH = "/database";
    private final Map<String, Block> schemas ;
    private final Deque<String> LRUQueue;
    private final FileReader dataReader = FileReader.getInstance(PATH);
    private final static LRUCache LRU_CACHE = new LRUCache();
    private LRUCache(){
        schemas = new HashMap<>();
        LRUQueue = new LinkedList<>();
    }
    public static LRUCache getInstance(){
        return LRU_CACHE;
    }
    @Override
    public Block getBlock(String blockName) {
        synchronized (schemas) {
            synchronized (LRUQueue) {
                if(schemas.containsKey(blockName)){
                    moveBlockToFront(blockName);
                    return schemas.get(blockName);
                }
                if(isBlockExist(blockName)){
                    uploadToCache(blockName);
                    return schemas.get(blockName);
                }
            }
        }
        return null;
    }
    private void moveBlockToFront(String schemaName){
        synchronized (LRUQueue){
            LRUQueue.remove(schemaName);
            LRUQueue.addFirst(schemaName);
        }
    }
    private synchronized boolean isBlockExist(String schemaName){
        return dataReader.isFileExist("",schemaName);
    }
    private void uploadToCache(String schemaName){
        synchronized (schemas){
            synchronized (LRUQueue){
                if(schemas.size() == MAX_SCHEMA_NUMBER){
                    String leastUsingSchema = LRUQueue.removeLast();
                    schemas.remove(leastUsingSchema);
                }
                Schema schema = (Schema) bringBlock(schemaName);
                LRUQueue.addFirst(schemaName);
                schemas.put(schemaName,schema);
            }
        }
    }
    private synchronized Block bringBlock(String blockName){
        int objectCount=0;
        File[] objectTypes = dataReader.getAllSubDirectories(blockName);
        HashMap<String, Type> types = new HashMap<>();
        for (File objectType:objectTypes ) {
            String typeName = objectType.getName();
            String objectTypeShortPath= blockName +"/"+typeName;
            Type type = new Type(blockName, typeName,MAX_Object_NUMBER);
            for(File object : objectType.listFiles()){
                String objectID = object.getName().replaceFirst("[.][^.]+$", "");
                JSONObject jsonObject = dataReader.readDataFromFile(objectTypeShortPath,object.getName());
                type.addObject(objectID,jsonObject);
                if (objectCount==MAX_Object_NUMBER)
                    break;
                objectCount++;
            }
            types.put(typeName,type);
        }
        return new Schema(types);
    }
    @Override
    public void update(){
        isDirty = true;
        dropCache();
    }
    private void dropCache(){
        if(isDirty){
            synchronized (schemas){
                synchronized (LRUQueue){
                    schemas.clear();
                    LRUQueue.clear();
                }
            }
        }
    }
}
