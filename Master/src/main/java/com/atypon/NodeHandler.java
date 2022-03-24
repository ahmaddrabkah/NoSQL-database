package com.atypon;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.IOException;
import java.util.*;

public class NodeHandler {
    private final FileReaderWriter dataReaderWriter = FileReaderWriter.getInstance("/database");

    public void handleNodeOperation(String operation, Map<String, String> data ) throws IOException {
        if(operation.equalsIgnoreCase("create"))
            createSchema(data);
        else if(operation.equalsIgnoreCase("addType"))
            addObjectType(data);
        else if(operation.equalsIgnoreCase("addObject"))
            addObject(data);
        else if(operation.equalsIgnoreCase("deleteObject"))
            deleteObject(data);
        else if(operation.equalsIgnoreCase("updateObject"))
            updateObject(data);

    }
    private void createSchema(Map<String, String> schemaInfo) throws IOException{
        String schemaName = schemaInfo.get("schemaName");
        schemaInfo.remove("schemaName");
        List<String> typesName = new ArrayList<>(schemaInfo.keySet());
        dataReaderWriter.createDirectory(schemaName);
        dataReaderWriter.createSubDirectories(schemaName,typesName);
        createTypesFile(schemaName,schemaInfo);
    }
    private void addObjectType(Map<String, String> schemaInfo ) throws IOException{
        String schemaName = schemaInfo.get("schemaName");
        schemaInfo.remove("schemaName");
        String objectTypeName = schemaInfo.keySet().toString();
        objectTypeName = objectTypeName.substring(1,objectTypeName.length()-1);
        dataReaderWriter.createDirectory(schemaName+"\\"+objectTypeName);
        createTypesFile(schemaName,schemaInfo);
    }
    private void createTypesFile(String schemaName, Map<String, String> schemaInfo) throws IOException {
        for (String type: schemaInfo.keySet()) {
            String directoryName = schemaName+"\\"+type;
            String fileName = type+"Structure";
            String data = schemaInfo.get(type);
            dataReaderWriter.createFile(directoryName,fileName+".txt");
            dataReaderWriter.writeDataToFile(directoryName,fileName+".txt",data);
        }
    }
    private void addObject(Map<String, String> objectInfo ){
        createObjectFile(objectInfo);
    }
    private void updateObject(Map<String, String> objectInfo ){
        deleteObjectFile( objectInfo);
        createObjectFile(objectInfo);
    }
    private void createObjectFile(Map<String, String> objectInfo){
        try {
            String schemaName = objectInfo.get("schemaName");
            String typeName = objectInfo.get("typeName");
            String object = objectInfo.get("object");
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(object);
            String directoryName = schemaName+"\\"+typeName;
            String fileName = (String)jsonObject.get("id");
            String data = jsonObject.toJSONString();
            dataReaderWriter.createFile(directoryName,fileName+".txt");
            dataReaderWriter.writeDataToFile(directoryName,fileName+".txt",data);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
    private void deleteObject(Map<String, String> objectInfo){
        deleteObjectFile( objectInfo);
    }
    private void deleteObjectFile(Map<String, String> objectInfo){
        String schemaName = objectInfo.get("schemaName");
        String typeName = objectInfo.get("typeName");
        String id = objectInfo.get("id");
        dataReaderWriter.deleteFile(schemaName+"\\"+typeName,id+".txt");
    }
}
