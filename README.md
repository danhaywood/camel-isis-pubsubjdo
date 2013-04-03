Camel Subscriber for Isis' PublishingService
===============================

An [Apache Camel](http://camel.apache.org)-based subscriber of events published by the [Apache Isis](http://isis.apache.org) [JDO Objectstore](http://isis.apache.org/components/objectstores/jdo/about.html)'s [implementation](http://isis.apache.org/components/objectstores/jdo/publishing-service-jdo.html) of Isis' own [PublishingService](http://isis.apache.org/core/services/publishing-service.html) API.


## Objective

The Isis `PublishingServiceJdo` implementation persisted events into a `PUBLISHEDEVENT` database table, with the payload of the event serialized into JSON as per the `RestfulObjectsSpecEventSerializer`.

This project defines a Camel-based subscriber that polls this database table for events, unmarshals them into an object representation of the serialized JSON, and then routes these events to an Apache CXF-based web service.

The different responsibilities have been factored out into separate modules, so if you fork this code, you should be able to reuse a good deal of this code.

## Design

There are five modules that make up this project.  Four constitute production code, one is intended for testing):

* bus

  This defines some very simple classes that represent the canonical format of
  the published events.  These classes - `ActionInvocationPayloadRepr` and
  `ObjectChangedPayloadRepr` - are subclasses of the `DomainObjectRepresentation` provided by the Restful Objects viewers' applib; basically wrappers around the
  JSON format.  Subscribers can easily reach into these JSON structures to 
  inspect the data that they require.
  
* publisher

  This module defines a class to act as a JPA endpoint acting as a producer (ie polling for events from the SQL Server `PUBLISHEDEVENT` table).  The JPA entity is also called `PublishedEvent`.

  The module also provides the Camel `DataFormat` classes to convert into the
  canonical formats defined in the `bus` module.

* soapsubscriber

  This module has a very simple WSDL and is configured to use an CXF Maven plugin for generating the interface definitions and client-side.

  The WSDL simply accepts a single string (intended to be the identifier of a `ToDoItem` that has been 'completed'); because the module is generated from the
  WSDL, it should be straight-forward enough to replace with the WSDL of some other web service.

  The module also defines a bean to extract (transform) the appropriate information from the canonical `bus` representation to call the web service.

* soapsubscriberstub

  This module is for testing only, and provides a simple implementation of the webservice which can be started/stopped from tests and provides a backdoor
  'spy' method to check if and how it has been called.  

* webconsole

  This module has dependencies on the `bus`, `publisher` and `soapsubscriber` modules, and contains the Camel route to take published events from JPA and selectively send them to the web service.

  The tests in this module also depend upon the `soapsubscriberstub` module,
  checking that the web service was called correctly.

The Camel route can be found in `webconsole/src/main/webapp/WEB-INF/applicationContext.xml` (in the `<camelContext>` element).


## Diagram

All the above as a pretty picture:

![](doc/sketch.png)



## Running

To run the `soapsubscriberstub`, just use

<pre>
  cd soapsubscriberstub
  mvn -P server
</pre>

You can test this service independently using the `org.apache.isis.example.wrj.todoitem.ToDoItemClient` class in `soapsubscriber` module.


To run the `webconsole`, just use

<pre>
  cd webconsole
  mvn clean package jetty:run
</pre>

Note that this cannot (easily) be run from the IDE because of the OpenJPA enhancement process that is required.


If you run up Isis with the ToDo quickstart app, then any ACTION_INVOCATION events (eg invoking the `complete()` action on a `ToDoItem`) should be routed through the webservice (and the href of the target of the event will appear in the console of the `soapsubscriberstub`) whereas any OBJECT_CHANGED events will be printed to the console of the `webconsole`.

