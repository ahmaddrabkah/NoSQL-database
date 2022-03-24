package com.atypon;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.util.*;

public class FileReaderWriter {
    private final String PATH;
    private static volatile FileReaderWriter dataReaderWriter = null ;

    private FileReaderWriter(String path){
        this.PATH=path;
    }
    public static FileReaderWriter getInstance(String path){
        if(dataReaderWriter == null){
            synchronized (FileReaderWriter.class){
                dataReaderWriter = new FileReaderWriter(path);
            }
        }
        return dataReaderWriter;
    }
    public synchronized List<JSONObject> readDataFromFile(String directoryName, String fileName){
        JSONParser jsonParser = new JSONParser();
        List<JSONObject> jsonArray = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(
                new FileReader(PATH+"\\"+directoryName+"\\"+fileName)
        )){
            String line;
            JSONObject object;
            while ((line = bufferedReader.readLine()) != null){
                object = (JSONObject)jsonParser.parse(line);
                jsonArray.add(object);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }
    public synchronized void createDirectory(String directoryName){
        File file = new File(PATH+"\\"+directoryName);
        file.mkdir();
    }
    public synchronized void createSubDirectories(String directoryName , List<String> subDirectoriesNames){
        File file ;
        for (String subDirectoryName:subDirectoriesNames) {
            file = new File(PATH+"\\"+directoryName+"\\"+subDirectoryName);
            file.mkdir();
        }
    }
    public synchronized void createFile(String directoryName, String fileName) throws IOException {
        File file = new File(PATH+"\\"+directoryName+"\\"+fileName);
        file.createNewFile();
    }
    public synchronized void writeDataToFile(String directoryName, String fileName, String data){
        try(BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(PATH+"\\"+directoryName+"\\"+fileName)
        )){
            JSONObject object = (JSONObject) new JSONParser().parse(data);
            bufferedWriter.write(object.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
    public synchronized void deleteFile(String directoryName, String fileName){
        File file = new File(PATH+"\\"+directoryName+"\\"+fileName);
        file.delete();
    }
}
