package com.danhaywood.camel.isispubsubjdo.publisher.canonicalize;

import org.codehaus.jackson.JsonNode;

import org.apache.isis.viewer.restfulobjects.applib.util.JsonMapper;

public class JsonUtil {
    
    private JsonUtil(){}

    public static JsonNode asJsonNode(String json)  {
        try {
            return JsonMapper.instance().read(json, JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
