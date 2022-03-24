package com.atypon.cache;

public interface Cache {
    Block getBlock(String blockName);
    void update();
}
