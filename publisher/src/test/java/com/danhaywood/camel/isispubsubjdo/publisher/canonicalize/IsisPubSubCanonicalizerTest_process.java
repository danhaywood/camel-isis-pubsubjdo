package com.danhaywood.camel.isispubsubjdo.publisher.canonicalize;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.bus.ExamplePayloads;
import com.danhaywood.camel.isispubsubjdo.bus.ObjectChangedPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent.EventType;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultExchange;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;

public class IsisPubSubCanonicalizerTest_process {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private CamelContext mockContext;
    
    private Exchange exchange;
    private Message message;
    
    private IsisPubSubCanonicalizer canonicalizer;

    private PublishedEvent actionInvocationEvent;
    private String actionInvocationPayloadJson;
    
    private PublishedEvent objectUpdatedEvent;
    private String objectChangedPayloadJson;

    private long now;

    @Before
    public void setUp() throws Exception {
        canonicalizer = new IsisPubSubCanonicalizer();
        exchange = new DefaultExchange(mockContext);
        message = exchange.getIn();
        
        now = Calendar.getInstance().getTimeInMillis();
        
        actionInvocationEvent = new PublishedEvent();
        actionInvocationEvent.setTransactionid("123");
        actionInvocationEvent.setSequence(0);
        actionInvocationEvent.setId("123-0");
        actionInvocationEvent.setEventType(EventType.ACTION_INVOCATION);
        actionInvocationEvent.setUser("fredbloggs");
        actionInvocationEvent.setTimestamp(now);
        actionInvocationEvent.setSerializedform(ExamplePayloads._001);
        
        actionInvocationPayloadJson = new JsonRepresentation(JsonUtil.asJsonNode(ExamplePayloads._001)).getMap("payload").toString();

        objectUpdatedEvent = new PublishedEvent();
        objectUpdatedEvent.setTransactionid("123");
        objectUpdatedEvent.setSequence(1);
        objectUpdatedEvent.setId("123-1");
        objectUpdatedEvent.setEventType(EventType.OBJECT_UPDATED);
        objectUpdatedEvent.setUser("fredbloggs");
        actionInvocationEvent.setTimestamp(now);
        objectUpdatedEvent.setSerializedform(ExamplePayloads._002);
        
        objectChangedPayloadJson = new JsonRepresentation(JsonUtil.asJsonNode(ExamplePayloads._002)).getMap("payload").toString();

    }

    @Test(expected=IllegalArgumentException.class)
    public void whenNoBody() throws Exception {
        message.setBody(null);
        canonicalizer.process(exchange);
    }

    @Test(expected=IllegalArgumentException.class)
    public void whenNotAPublishedEvent() throws Exception {
        message.setBody("foobar");
        canonicalizer.process(exchange);
    }

    @Test
    public void whenAnActionInvocationEvent() throws Exception {
        message.setBody(actionInvocationEvent);
        canonicalizer.process(exchange);
        
        assertThat(exchange.hasOut(), is(false));
        Object processedBody = exchange.getIn().getBody();
        
        assertThat(processedBody instanceof ActionInvocationPayloadRepr, is(true));
        
        ActionInvocationPayloadRepr repr = (ActionInvocationPayloadRepr)processedBody;
        assertThat(repr.toString(), is(actionInvocationPayloadJson));
        
        Object isisPublishedEventObj = message.getHeader("isisPublishedEvent");

        assertThat(isisPublishedEventObj instanceof Map, is(true));
        Map<String,Object> isisPublishedEvent = (Map<String, Object>) isisPublishedEventObj;
        
        assertThat((String)isisPublishedEvent.get("eventType"), is("ACTION_INVOCATION"));
        assertThat((String)isisPublishedEvent.get("transactionId"), is("123"));
        assertThat((Integer)isisPublishedEvent.get("sequence"), is(0));
        assertThat((String)isisPublishedEvent.get("id"), is("123-0"));
        assertThat((String)isisPublishedEvent.get("user"), is("fredbloggs"));
        assertThat((Long)isisPublishedEvent.get("timestamp"), is(now));

        Object isisExtendedEventTypeObj = message.getHeader("isisExtendedEventType");

        assertThat(isisExtendedEventTypeObj instanceof String, is(true));
        String isisExtendedEventType = (String) isisExtendedEventTypeObj;
        
        assertThat(isisExtendedEventType, is("ACTION_INVOCATION;dom.todo.ToDoItem#completed()"));
    }

    @Test
    public void whenAnObjectUpdatedEvent() throws Exception {
        message.setBody(objectUpdatedEvent);
        canonicalizer.process(exchange);
        
        assertThat(exchange.hasOut(), is(false));
        Object processedBody = exchange.getIn().getBody();

        assertThat(processedBody instanceof ObjectChangedPayloadRepr, is(true));
        
        ObjectChangedPayloadRepr repr = (ObjectChangedPayloadRepr)processedBody;
        assertThat(repr.toString(), is(objectChangedPayloadJson));

        Object isisExtendedEventTypeObj = message.getHeader("isisExtendedEventType");

        assertThat(isisExtendedEventTypeObj instanceof String, is(true));
        String isisExtendedEventType = (String) isisExtendedEventTypeObj;
        
        assertThat(isisExtendedEventType, is("OBJECT_UPDATED;dom.todo.ToDoItem"));
    }

}
