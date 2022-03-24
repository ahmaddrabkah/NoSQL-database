package com.atypon.loadbalance;

public interface Subject {
     void register(Observer observer);
     void unregister(Observer observer);
     void notifyUpdate();
}
