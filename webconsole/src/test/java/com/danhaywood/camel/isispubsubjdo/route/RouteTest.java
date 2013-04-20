package com.danhaywood.camel.isispubsubjdo.route;


import static org.hamcrest.CoreMatchers.is;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.bus.ExamplePayloads;
import com.danhaywood.camel.isispubsubjdo.bus.ObjectChangedPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent;
import com.danhaywood.camel.isispubsubjdo.publisher.db.PublishedEvent.EventType;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;

@ContextConfiguration
public class RouteTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/danhaywood/camel/isispubsubjdo/route/RouteTest.xml");
    }

    @EndpointInject(uri = "mock:actionInvocationEndpoint")
    protected MockEndpoint actionInvocationEndpoint;

    @EndpointInject(uri = "mock:objectCreatedEndpoint")
    protected MockEndpoint objectCreatedEndpoint;

    @EndpointInject(uri = "mock:objectUpdatedEndpoint")
    protected MockEndpoint objectUpdatedEndpoint;

    @EndpointInject(uri = "mock:objectDeletedEndpoint")
    protected MockEndpoint objectDeletedEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    private PublishedEvent publishedEvent;

    @Before
    public void fixture() throws Exception {
        publishedEvent = new PublishedEvent();
        publishedEvent.setId("123-2");
        publishedEvent.setSequence(2);
        publishedEvent.setTransactionid("123");
        publishedEvent.setSerializedform("");
    }
    
    @DirtiesContext
    @Test
    public void routeToActionInvocation() throws Exception {

        publishedEvent.setEventType(EventType.ACTION_INVOCATION);
        publishedEvent.setSerializedform(ExamplePayloads._001);
        
        actionInvocationEndpoint.expectedMessageCount(1);
        objectCreatedEndpoint.expectedMessageCount(0);
        objectUpdatedEndpoint.expectedMessageCount(0);
        objectDeletedEndpoint.expectedMessageCount(0);
        
        template.sendBody(publishedEvent);

        actionInvocationEndpoint.assertIsSatisfied();
        objectUpdatedEndpoint.assertIsSatisfied();

        Exchange exchange = actionInvocationEndpoint.getReceivedExchanges().get(0);
        ActionInvocationPayloadRepr repr = exchange.getIn().getBody(ActionInvocationPayloadRepr.class);
        JsonRepresentation targetProperty = repr.getProperty("target");
        String targetHref = targetProperty.getString("value.href");
        
        assertThat(targetHref, is("http://localhost:8080/restful/objects/TODO:L_11^2:sven:1364901239786"));
    }

    
    @DirtiesContext
    @Test
    public void routeToObjectUpdated() throws Exception {

        publishedEvent.setEventType(EventType.OBJECT_UPDATED);
        publishedEvent.setSerializedform(ExamplePayloads._002);
        
        actionInvocationEndpoint.expectedMessageCount(0);
        objectCreatedEndpoint.expectedMessageCount(0);
        objectUpdatedEndpoint.expectedMessageCount(1); // 1 here
        objectDeletedEndpoint.expectedMessageCount(0);

        
        template.sendBody(publishedEvent);

        actionInvocationEndpoint.assertIsSatisfied();
        objectUpdatedEndpoint.assertIsSatisfied();

        Exchange exchange = objectUpdatedEndpoint.getReceivedExchanges().get(0);
        ObjectChangedPayloadRepr repr = exchange.getIn().getBody(ObjectChangedPayloadRepr.class);
        JsonRepresentation targetProperty = repr.getProperty("changed");
        String targetHref = targetProperty.getString("value.href");
        
        assertThat(targetHref, is("http://localhost:8080/restful/objects/TODO:L_11^2:sven:1364901239786"));
    }

    @DirtiesContext
    @Test
    public void ignoresRouteToActionInvocationNotMatched() throws Exception {

        publishedEvent.setEventType(EventType.ACTION_INVOCATION);
        publishedEvent.setSerializedform(ExamplePayloads._003);
        
        actionInvocationEndpoint.expectedMessageCount(0);
        objectCreatedEndpoint.expectedMessageCount(0);
        objectUpdatedEndpoint.expectedMessageCount(0);
        objectDeletedEndpoint.expectedMessageCount(0);
        
        template.sendBody(publishedEvent);

        actionInvocationEndpoint.assertIsSatisfied();
        objectUpdatedEndpoint.assertIsSatisfied();
    }

}
