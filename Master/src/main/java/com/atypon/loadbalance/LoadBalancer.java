package com.atypon.loadbalance;

import java.util.*;

public class LoadBalancer implements Subject {
    private final List<Observer> nodes = new ArrayList<>();
    private Node currentAvailableNode;

    public LoadBalancer(){
        createNode();
    }
    public synchronized Observer getAvailableNode(){
        if(isCurrentNodeFull())
            createNode();
        currentAvailableNode.incrementNumberOfConnections();
        return currentAvailableNode;
    }
    private void createNode(){
        Node node = Node.nodeFactory();
        currentAvailableNode = node;
        register(node);
    }
    private boolean isCurrentNodeFull(){

        return currentAvailableNode.getNumberOfConnections()+1 > 2;
    }
    @Override
    public void register(Observer observer) {
        nodes.add(observer);
    }
    @Override
    public void unregister(Observer observer) {
        nodes.remove(observer);
    }
    @Override
    public void notifyUpdate() {
        synchronized (nodes){
            for (Observer node: nodes) {
                node.update();
            }
        }
    }
}
