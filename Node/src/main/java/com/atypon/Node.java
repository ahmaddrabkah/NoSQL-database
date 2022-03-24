package com.atypon;


import com.atypon.cache.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Node {
    private final Cache cache = LRUCache.getInstance();
    public static void main(String[] args){
        System.out.println("Node start !!");
        new Node();
    }
    public Node(){
        try {
            ServerSocket serverSocket
                    = new ServerSocket(5000);
            ExecutorService executorService
                    = Executors.newCachedThreadPool();
            while (true){
                Socket socket = serverSocket.accept();
                executorService.execute(new Task(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class Task implements Runnable{
        private ObjectInputStream inputFromUser;
        private ObjectOutputStream outputToUser;
        private final Socket clientSocket;
        private Socket masterSocket;
        private ObjectInputStream inputFromMaster;
        private ObjectOutputStream outputToMaster;

        public Task(Socket socket){
            this.clientSocket= socket;
        }
        @Override
        public void run() {
            try {
                inputFromUser = new ObjectInputStream(clientSocket.getInputStream());
                outputToUser = new ObjectOutputStream(clientSocket.getOutputStream());
                ClientHandler clientHandler = new ClientHandler(inputFromUser,outputToUser);
                while (true){
                    outputToUser.writeObject("\n1.Create Schema" +
                            "\n2.Add Object type to schema \n3.Add Object" +
                            "\n4.Delete Object \n5.Update Object" +
                            "\n6.Read Object \n7.Read All Objects \n8.Exit" +
                            "\nEnter your choice number:");
                    String input = (String)inputFromUser.readObject();
                    if(input.matches("[1-7]")){
                        Object[] objects = clientHandler.handleClientOperation(input);
                        if(objects != null){
                            String operation = (String) objects[0];
                            Object data = objects[1];
                            sendDataToMaster(operation,data);
                        }
                    }
                    else if(input.equalsIgnoreCase("update"))
                    {
                        update();
                        break;
                    }
                    else if(input.equals("8"))
                        break;
                    else
                        outputToUser.writeObject("Invalid choice");
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    inputFromUser.close();
                    outputToUser.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void update(){
            cache.update();
            try {
                inputFromUser.close();
                outputToUser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void sendDataToMaster(String operation,Object data){
            try {
                masterSocket = new Socket("192.168.1.24",8100);
                outputToMaster = new ObjectOutputStream(masterSocket.getOutputStream());
                inputFromMaster = new ObjectInputStream(masterSocket.getInputStream());
                outputToMaster.writeObject("Node");
                outputToMaster.writeObject(operation);
                outputToMaster.writeObject(data);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    inputFromMaster.close();
                    outputToMaster.close();
                    masterSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
