package com.danhaywood.camel.isispubsubjdo.publisher.dataformat;

import java.io.InputStream;
import java.io.OutputStream;

import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

/**
 * A Camel {@link DataFormat} that can unmarshal the {@link PublishedEvent#getSerializedform() payload} of an {@link PublishedEvent}
 * into a RestfulObjects {@link DomainObjectRepresentation representation}.
 * 
 * <p>
 * If using Spring DSL, register as follows: 
 * <pre>
 *  &lt;bean id=&quot;actionInvocationDataFormat&quot; class=&quot;com.danhaywood.camel.isispubsubjdo.publisher.dataformat.IsisPubSubPayloadDataFormat&quot;&gt;
 *      &lt;property name=&quot;payloadClass&quot;&gt;
 *          &lt;value&gt;com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr&lt;/value&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 *  &lt;bean id=&quot;objectChangedDataFormat&quot; class=&quot;com.danhaywood.camel.isispubsubjdo.publisher.dataformat.IsisPubSubPayloadDataFormat&quot;&gt;
 *      &lt;property name=&quot;payloadClass&quot;&gt;
 *          &lt;value&gt;com.danhaywood.camel.isispubsubjdo.bus.ObjectChangedPayloadRepr&lt;/value&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * <p>
 * Then, for a route where the message contains a {@link PublishedEvent}, route using something like:
 * <pre>
 *   ...
 *   &lt;camel:choice&gt;
 *       &lt;camel:when&gt;
 *           &lt;camel:ognl&gt;request.body.eventType.toString() == 'ACTION_INVOCATION'&lt;/camel:ognl&gt;
 *           &lt;camel:transform&gt;
 *               &lt;camel:simple&gt;${in.body.serializedform}&lt;/camel:simple&gt;
 *           &lt;/camel:transform&gt;
 *           &lt;camel:unmarshal ref=&quot;actionInvocationDataFormat&quot;/&gt;
 *           ...
 *      &lt;/camel:when&gt;
 *  &lt;/camel:choice&gt;
 * </pre>
 *
 * @see IsisPubSubPayloadDataFormatForActionInvocation
 * @see IsisPubSubPayloadDataFormatForObjectChanged
 */
public class IsisPubSubPayloadDataFormat implements DataFormat {

    private Class<? extends JsonRepresentation> payloadClass;

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        Object body = exchange.getIn().getBody();
        if(!(body instanceof String)) {
            return null;
        } 
        final PublishedEventSerializedFormRepr serializedFormRepr = new PublishedEventSerializedFormRepr((String)body);
        final JsonRepresentation payloadRepr = serializedFormRepr.getRepresentation("payload").as(payloadClass);
        return payloadRepr;
    }
    
    public void setPayloadClass(Class<? extends JsonRepresentation> payloadClass) {
        this.payloadClass = payloadClass;
    }

}
