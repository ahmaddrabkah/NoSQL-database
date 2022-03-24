package com.atypon;

import com.atypon.loadbalance.*;
import org.json.simple.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class MasterController {
    private final Map<String , JSONObject> users = new HashMap<>();
    private final FileReaderWriter dataReaderWriter =
            FileReaderWriter.getInstance("C:\\Users\\user\\Desktop\\Final_Project\\Database");
    private final LoadBalancer loadBalancer = new LoadBalancer();
    public static void main(String[] args){
        new MasterController();
    }

    public MasterController(){
        loadUsers();
        try {
            ServerSocket serverSocket = new ServerSocket(8100);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (true){
                Socket socket = serverSocket.accept();
                executorService.execute(new Task(socket));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private  void loadUsers(){
        List<JSONObject> jsonArray= dataReaderWriter.readDataFromFile("Users","users.txt");
        String username;
        for (JSONObject object: jsonArray) {
            username = (String)object.get("username");
            users.put(username,object);
        }
    }
    private class Task implements Runnable{
        private ObjectInputStream input;
        private ObjectOutputStream output;
        Socket socket;
        public Task(Socket socket){
            this.socket= socket;
        }
        @Override
        public void run() {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                String clientOrNode = (String)input.readObject();
                if(clientOrNode.equalsIgnoreCase("client"))
                    login();
                else
                    nodeOperation();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    input.close();
                    output.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void login() throws IOException, ClassNotFoundException {
            String username = (String)input.readObject();
            String password = (String)input.readObject();
            while( !validateFromUser(username,password)){
                output.writeObject(false);
                output.writeObject("Invalid Credentials, please try again");
                username = (String)input.readObject();
                password = (String)input.readObject();
            }
            output.writeObject(true);
            output.writeObject(getAvailableNodePort());
        }
        public int getAvailableNodePort(){
            Node availableNode = (Node)loadBalancer.getAvailableNode();
            return availableNode.getPortNumber();
        }
        public boolean validateFromUser(String username, String password){
            JSONObject tempUser = users.get(username);
            if(tempUser == null)
                return false;
            String tempUsername = (String)tempUser.get("username");
            String tempPassword = (String)tempUser.get("password");
            return username.equals(tempUsername) && password.equals(tempPassword);
        }
        public void nodeOperation() throws IOException, ClassNotFoundException {
            String operation = (String)input.readObject();
            Map<String, String> data = (Map<String, String>)input.readObject();
            NodeHandler nodeHandler = new NodeHandler();
            nodeHandler.handleNodeOperation(operation,data);
            loadBalancer.notifyUpdate();
        }
    }
}


