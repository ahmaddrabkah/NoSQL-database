package com.atypon.cache;

import java.util.HashMap;
import java.util.Map;

public class Schema implements Block{
    private final Map<String, Type> schemaTypes;

    public Schema(Map<String, Type> schemaData){
        this.schemaTypes = schemaData;
    }
    @Override
    public Object getBlockData() {
        return schemaTypes;
    }
    public Type getType(String typeName){
        if(containsType(typeName))
            return schemaTypes.get(typeName);
        return null;
    }
    public boolean containsType(String typeName){
        return schemaTypes.containsKey(typeName);
    }
}
