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

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceContext;

public class ToDoItemImpl implements ToDoItem {
    
    /**
     * The WebServiceContext can be used to retrieve special attributes like the 
     * user principal. Normally it is not needed
     */
    @Resource
    WebServiceContext wsContext;

    @Override
//    @WebResult(name = "out", targetNamespace = "")
//    @RequestWrapper(localName = "Processed", targetNamespace = "http://isis.apache.org/example/wrj/ToDoItem/", className = "org.apache.isis.example.wrj.todoitem.Processed")
//    @WebMethod(operationName = "Processed", action = "http://isis.apache.org/example/wrj/ToDoItem/Processed")
//    @ResponseWrapper(localName = "ProcessedResponse", targetNamespace = "http://isis.apache.org/example/wrj/ToDoItem/", className = "org.apache.isis.example.wrj.todoitem.ProcessedResponse")
    public String processed(@WebParam(name = "in", targetNamespace = "") String in) {
        System.out.println(in);
        return "OK";
    }

}
