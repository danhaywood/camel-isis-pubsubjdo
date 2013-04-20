package com.danhaywood.camel.isispubsubjdo.publisher.canonicalize;

import java.util.Map;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.bus.ObjectChangedPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent.EventType;
import com.google.common.collect.Maps;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spi.DataFormat;

import org.apache.isis.viewer.restfulobjects.applib.domainobjects.DomainObjectRepresentation;

/**
 * A Camel {@link DataFormat} that can unmarshal a {@link Message} whose {@link Message#getBody() body} 
 * contains a {@link PublishedEvent}, into a RestfulObjects {@link DomainObjectRepresentation representation}
 * with a number of supporting {@link Message#getHeaders() headers}.
 * 
 * <p>
 * The headers that are attached are:
 * <ul>
 * <li><tt>isisPublishedEvent</tt> - a map, which in turn has keys corresponding to the properties of the {@link PublishedEvent}:
 *  <ul>
 *      <li><tt>id</tt> - a String, the concatentation of <tt>transactionId</tt> and <tt>sequence</tt></li>
 *      <li><tt>transactionid</tt> - a String, representing the Isis transaction in which this event was published</li>
 *      <li><tt>sequence</tt> - an int, being the 0-based sequence number of this event within the transaction (when more than one event is published)</li>
 *      <li><tt>eventType</tt> - a string, either <tt>ACTION_INVOCATION</tt>, <tt>OBJECT_CREATED</tt>, <tt>OBJECT_UPDATED</tt> or <tt>OBJECT_DELETED</tt></li>
 *      <li><tt>timestamp</tt> - a long, the epoch-based timestamp that the event was original published</li>
 *      <li><tt>user</tt> - a string, an identifier of the user whose action caused the event to be published</li>
 *  </ul>
 *  </li>
 *  <li><tt>isisExtendedEventType</tt> - uniquely identifies the event type.
 *      <p>This is a concatenation of the <tt>isisPublishedEvent[eventType]</tt> along with an identifier (obtained from the
 *      payload) to uniquely identify the event at a more fine-grained level.</p>
 *      <p>For example:</p>
 *      <ul>
 *      <li><tt>ACTION_INVOCATION;dom.todo.ToDoItem#completed()</tt></li>
 *      <li><tt>OBJECT_CREATED;dom.todo.ToDoItem</tt></li>
 *      <li><tt>OBJECT_UPDATED;dom.todo.ToDoItem</tt></li>
 *      <li><tt>OBJECT_DELETED;dom.todo.ToDoItem</tt></li>
 *      </ul>
 *      <p>In most cases, it should be sufficient to look only at this header to route the event. 
 *  </li> 
 * </ul>
 * <p>
 * If using Spring DSL, register as follows: 
 * <pre>
 *  &lt;bean id=&quot;isisCanonicalize&quot; class=&quot;com.danhaywood.camel.isispubsubjdo.publisher.canonicalize.IsisPubSubCanonicalizer&quot;/&gt;
 * </pre>
 * 
 * <p>
 * Then, for a route where the message contains a {@link PublishedEvent}, route using something like:
 * <pre>
 *   ...
 *   &lt;camel:process ref=&quot;isisCanonicalize&quot;/&gt;
 *   &lt;camel:choice&gt;
 *       &lt;camel:when&gt;
 *           &lt;camel:simple&gt;${header.isisExtendedEventType} == 'ACTION_INVOCATION;dom.todo.ToDoItem#completed()'&lt;/camel:simple&gt;
 *           ...
 *      &lt;/camel:when&gt;
 *  &lt;/camel:choice&gt;
 * </pre>
 */
public class IsisPubSubCanonicalizer implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();
        Object body = inMessage.getBody();
        if(!(body instanceof PublishedEvent)) {
            throw new IllegalArgumentException("Expected body to contain a PublishedEvent");
        } 
        final PublishedEvent publishedEvent = (PublishedEvent)body;
        
        final Map<String, Object> isisPublishedEvent = isisPublishedEventHeaderValueFor(publishedEvent);
        inMessage.setHeader("isisPublishedEvent", isisPublishedEvent);
        
        final DomainObjectRepresentation representation = domainObjectReprFor(publishedEvent);
        final String isisExtendedEventType = isisExtendedEventTypeHeaderValueFor(publishedEvent, representation);
        inMessage.setHeader("isisExtendedEventType", isisExtendedEventType);
        
        Class<? extends DomainObjectRepresentation> payloadClass = 
                publishedEvent.getEventType() == EventType.ACTION_INVOCATION 
                    ? ActionInvocationPayloadRepr.class 
                    : ObjectChangedPayloadRepr.class;
        
        final DomainObjectRepresentation payloadRepr = representation.as(payloadClass);
        inMessage.setBody(payloadRepr);
    }

    private static Map<String, Object> isisPublishedEventHeaderValueFor(PublishedEvent publishedEvent) {
        Map<String, Object> isisPublishedEvent = Maps.newHashMap();
        isisPublishedEvent.put("transactionId", publishedEvent.getTransactionid());
        isisPublishedEvent.put("sequence", publishedEvent.getSequence());
        isisPublishedEvent.put("id", publishedEvent.getId());
        isisPublishedEvent.put("eventType", publishedEvent.getEventType().toString());
        isisPublishedEvent.put("timestamp", publishedEvent.getTimestamp());
        isisPublishedEvent.put("user", publishedEvent.getUser());
        return isisPublishedEvent;
    }

    private static String isisExtendedEventTypeHeaderValueFor(final PublishedEvent publishedEvent, final DomainObjectRepresentation representation) {
        final String propertyName = publishedEvent.getEventType() == EventType.ACTION_INVOCATION ? "actionName" : "className";
        final String qualifier = representation.getProperty(propertyName).getString("value"); 
        final String isisExtendedEventType = publishedEvent.getEventType().toString() + ";" + qualifier;
        return isisExtendedEventType;
    }

    private static DomainObjectRepresentation domainObjectReprFor(final PublishedEvent publishedEvent) {
        final PublishedEventSerializedFormRepr serializedFormRepr = new PublishedEventSerializedFormRepr(publishedEvent.getSerializedform());
        final DomainObjectRepresentation representation = serializedFormRepr.getRepresentation("payload").as(DomainObjectRepresentation.class);
        return representation;
    }


}
