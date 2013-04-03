/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.isis.example.wrj.todoitem;

import java.util.List;

import javax.xml.ws.Endpoint;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;

public class ToDoItemServer {

    private static final int DEFAULT_PORT = 9090;
    private EndpointImpl ep;
    private ToDoItemImpl implementor;

    public ToDoItemServer() throws Exception {
        this(DEFAULT_PORT);
    }

    public ToDoItemServer(int port) {
        System.out.println("Starting Server on port " + port);
        implementor = new ToDoItemImpl();
        ep = (EndpointImpl)Endpoint.publish("http://localhost:" + port + "/ToDoItemPort", implementor);

        // Adding logging for incoming and outgoing messages
        ep.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
        ep.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
    }

    public static void main(String args[]) throws Exception {
        new ToDoItemServer();
        System.out.println("Server ready...");
//        Thread.sleep(5 * 60 * 1000);
//        System.out.println("Server exiting");
//        System.exit(0);
    }

    public void stop() {
        ep.getServer().stop();
        ep.getServer().destroy();
    }

    public List<Object> getCallArgs() {
        return implementor.getCallArgs();
    }
}
