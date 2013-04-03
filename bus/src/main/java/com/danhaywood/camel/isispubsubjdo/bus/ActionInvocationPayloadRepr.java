package com.danhaywood.camel.isispubsubjdo.bus;


import org.codehaus.jackson.JsonNode;

import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

public class ActionInvocationPayloadRepr extends DomainObjectRepresentation {

    public ActionInvocationPayloadRepr(JsonNode jsonNode) {
        super(jsonNode);
    }

    public ActionInvocationPayloadRepr(String json) {
        this(Util.asJsonNode(json));
    }

}
