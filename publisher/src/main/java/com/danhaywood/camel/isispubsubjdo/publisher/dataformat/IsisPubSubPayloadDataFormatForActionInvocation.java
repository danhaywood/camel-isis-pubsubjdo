package com.danhaywood.camel.isispubsubjdo.publisher.dataformat;

import com.danhaywood.camel.isispubsubjdo.bus.ActionInvocationPayloadRepr;


/**
 * Convenience subclass of {@link IsisPubSubPayloadDataFormat}.
 * 
 * <p>
 * If using Spring DSL, register as follows: 
 * <pre>
 *    &lt;bean id=&quot;actionInvocationDataFormat&quot; class=&quot;com.danhaywood.camel.isispubsubjdo.publisher.dataformat.IsisPubSubPayloadDataFormatForActionInvocation&quot;/&gt;
 * </pre>
 */
public class IsisPubSubPayloadDataFormatForActionInvocation extends IsisPubSubPayloadDataFormat{

    public IsisPubSubPayloadDataFormatForActionInvocation() {
        setPayloadClass(ActionInvocationPayloadRepr.class);
    }

}
