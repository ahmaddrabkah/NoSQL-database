package com.atypon.loadbalance;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Node implements Observer{
    private final String ip = "localhost";
    private int portNumber;
    private int numberOfConnections=0;

    public static Node nodeFactory(){
        Node node = new Node();
        int portUsed = createContainer();
        node.setPortNumber(portUsed);
        return node;
    }
    private static int createContainer(){
        String volumePath = "//c/Users/user/Desktop/Final_Project/Database";
        int portNumber = generateRandomPortNumber();
        try {
            String[] command = {"docker", "run", "-t", "--volume",
                    volumePath+":/database", "-p", portNumber+":5000", "adrabkah/node:v1"};
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process proc = pb.start();
            Thread.sleep(1500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return portNumber;
    }
    private static int generateRandomPortNumber(){
        Random random = new Random();
        return random.nextInt(6000);
    }
    public int getPortNumber() {
        return portNumber;
    }
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
    public int getNumberOfConnections() {
        return numberOfConnections;
    }
    public void incrementNumberOfConnections(){numberOfConnections++;}
    @Override
    public void update() {
        try{
            Socket socket = new Socket(ip,portNumber);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject("update");
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
