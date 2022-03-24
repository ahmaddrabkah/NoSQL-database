package com.atypon;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileReader {
    private final String PATH ;
    private static volatile FileReader dataReader=null;

    private FileReader(String path){
        this.PATH=path;
    }
    public static FileReader getInstance(String path){
        if(dataReader == null){
            synchronized (FileReader.class){
                dataReader = new FileReader(path);
            }
        }
        return dataReader;
    }
    public synchronized File[] getAllSubDirectories(String directoryName){
        String directoryPath = PATH;
        if(directoryName != null)
            directoryPath += "/"+directoryName;
        File file = new File(directoryPath);
        return file.listFiles();
    }
    public synchronized HashMap<String, JSONObject> getAllFilesDataInDirectory(String directoryName){
        String directoryPath = PATH+"/"+directoryName;
        File [] files = new File(directoryPath).listFiles();
        HashMap<String, JSONObject> map = new HashMap<>();
        if (files != null) {
            for (File file:files) {
                JSONObject object = readDataFromFile(directoryName,file.getName());
                String fileName =file.getName().replaceFirst("[.][^.]+$", "");
                map.put(fileName,object);
            }
        }
        return map;
    }
    public synchronized JSONObject readDataFromFile(String directoryName, String fileName){
        String filePath = PATH+"/"+directoryName+"/"+fileName;
        String data=null;
        JSONObject object = null;
        try(BufferedReader bufferedReader = new BufferedReader(
                new java.io.FileReader(filePath)
        )){
            data = bufferedReader.readLine().trim();
            object = (JSONObject) new JSONParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
    public synchronized boolean isFileExist(String directoryName,String fileName) {
        File[] allFolders = dataReader.getAllSubDirectories(directoryName);
        List<String> allDirectories=Arrays.asList(allFolders)
                .stream().map(File::getName)
                .collect(Collectors.toList());
        return allDirectories.contains(fileName);
    }
}
