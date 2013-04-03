package com.danhaywood.camel.isispubsubjdo.publisher.dataformat;


import com.danhaywood.camel.isispubsubjdo.publisher.util.Util;

import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

public class PublishedEventSerializedFormRepr extends DomainObjectRepresentation {

    public PublishedEventSerializedFormRepr(String json) {
        super(Util.asJsonNode(json));
    }

}
