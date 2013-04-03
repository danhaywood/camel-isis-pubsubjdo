package com.danhaywood.camel.isispubsubjdo.soapsubscriber;


import static org.hamcrest.CoreMatchers.is;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;
import com.danhaywood.camel.isispubsubjdo.bus.ExamplePayloads;
import com.danhaywood.camel.isispubsubjdo.publisher.dataformat.PublishedEventSerializedFormRepr;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.apache.isis.example.wrj.todoitem.ToDoItemServer;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;

@ContextConfiguration
public class InvokeWebServiceTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/danhaywood/camel/isispubsubjdo/soapsubscriber/InvokeWebServiceTest.xml");
    }

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    
    
    private ToDoItemServer toDoItemServer;


    private ActionInvocationPayloadRepr repr;

    
    @Before
    public void startWebService() throws Exception {
        toDoItemServer = new ToDoItemServer(9191);
    }
    
    @Before
    public void fixtureData() throws Exception {
        PublishedEventSerializedFormRepr serializedFormRepr = new PublishedEventSerializedFormRepr(ExamplePayloads._001);
        repr = serializedFormRepr.getRepresentation("payload").as(ActionInvocationPayloadRepr.class);
    }
    
    @After
    public void stopWebService() throws Exception {
        toDoItemServer.stop();
    }
    
    
    @DirtiesContext
    @Test
    public void callsWebService() throws Exception {

        template.sendBody(repr);

        JsonRepresentation targetProperty = repr.getProperty("target");
        Object targetHref = targetProperty.getString("value.href");
        
        assertThat(toDoItemServer.getCallArgs().get(0), is(targetHref));
    }

}
