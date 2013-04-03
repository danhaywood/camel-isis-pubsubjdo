package com.danhaywood.camel.isispubsubjdo.bus;

import org.codehaus.jackson.JsonNode;

import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

public class ObjectChangedPayloadRepr extends DomainObjectRepresentation {

    public ObjectChangedPayloadRepr(JsonNode jsonNode) {
        super(jsonNode);
    }

}
