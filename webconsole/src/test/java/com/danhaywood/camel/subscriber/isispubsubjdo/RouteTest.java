package com.danhaywood.camel.subscriber.isispubsubjdo;

import java.net.URL;
import java.nio.charset.Charset;

import com.danhaywood.camel.subscriber.isispubsubjdo.source.PublishedEvent;
import com.google.common.io.Resources;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class RouteTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/danhaywood/camel/subscriber/isispubsubjdo/RouteTest.xml");
    }

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Ignore
    @DirtiesContext
    @Test
    public void testSendMatchingMessage() throws Exception {
        String expectedBody = "<matched/>";


        PublishedEvent publishedEvent = new PublishedEvent();
        publishedEvent.setId("123-2");
        publishedEvent.setSequence(2);
        publishedEvent.setTransactionid("123");
        publishedEvent.setSerializedform("");
        
        String fileName = "001-Action-ToDoItem#completed.json";
        URL url = Resources.getResource(RouteTest.class, fileName);
        String json = Resources.toString(url, Charset.forName("UTF-8"));
        publishedEvent.setSerializedform(json);
        
        resultEndpoint.expectedBodiesReceived(publishedEvent);
        template.sendBodyAndHeader(publishedEvent, "foo", "bar");

        resultEndpoint.assertIsSatisfied();
    }

    @Ignore
    @DirtiesContext
    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);

        template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");

        resultEndpoint.assertIsSatisfied();
    }

    

}
