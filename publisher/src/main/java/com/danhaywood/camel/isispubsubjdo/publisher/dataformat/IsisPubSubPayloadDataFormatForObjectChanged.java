package com.danhaywood.camel.isispubsubjdo.publisher.dataformat;

import com.danhaywood.camel.isispubsubjdo.bus.ObjectChangedPayloadRepr;

/**
 * Convenience subclass of {@link IsisPubSubPayloadDataFormat}.
 * 
 * <p>
 * If using Spring DSL, register as follows: 
 * <pre>
 *    &lt;bean id=&quot;actionInvocationDataFormat&quot; class=&quot;com.danhaywood.camel.isispubsubjdo.publisher.dataformat.IsisPubSubPayloadDataFormatForObjectChanged&quot;/&gt;
 * </pre>
 */
public class IsisPubSubPayloadDataFormatForObjectChanged extends IsisPubSubPayloadDataFormat{

    public IsisPubSubPayloadDataFormatForObjectChanged() {
        setPayloadClass(ObjectChangedPayloadRepr.class);
    }

}
