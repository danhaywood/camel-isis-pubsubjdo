package com.danhaywood.camel.isispubsubjdo.publisher.util;

import org.codehaus.jackson.JsonNode;

import org.apache.isis.viewer.restfulobjects.applib.util.JsonMapper;

public class Util {
    
    private Util(){}

    public static JsonNode asJsonNode(String json)  {
        try {
            return JsonMapper.instance().read(json, JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
