package com.danhaywood.camel.isispubsubjdo.soapsubscriber;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;

public class ActionInvocationRepr2ToDoItemWebServiceArg0Transformer  {

    public String doTransform(ActionInvocationPayloadRepr repr) {
        final JsonRepresentation targetProperty = repr.getProperty("target");
        final String targetHref = targetProperty.getString("value.href");
        
        return targetHref;
    }
}
