package com.danhaywood.camel.isispubsubjdo.publisher.canonicalize;



import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

public class PublishedEventSerializedFormRepr extends DomainObjectRepresentation {

    public PublishedEventSerializedFormRepr(String json) {
        super(JsonUtil.asJsonNode(json));
    }

}
