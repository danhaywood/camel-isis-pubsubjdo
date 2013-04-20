package com.danhaywood.camel.isispubsubjdo.publisher.db;

import java.io.Serializable;
import javax.persistence.*;

import org.apache.camel.component.jpa.Consumed;


/**
 * The persistent class for the PUBLISHEDEVENT database table.
 * 
 */
@Entity
@Table(name="PUBLISHEDEVENT")
@NamedQuery(name = "queued", query = "select x from PublishedEvent x where x.state = 'QUEUED'")
public class PublishedEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum EventType {
	    ACTION_INVOCATION,
	    OBJECT_CREATED,
	    OBJECT_UPDATED,
	    OBJECT_DELETED,
	}
	
	@Id
	@Column(name="ID")
	private String id;

	@Column(name="\"SEQUENCE\"")
	private int sequence;

	@Column(name="SERIALIZEDFORM")
	private String serializedform;

	@Enumerated(EnumType.STRING)
    @Column(name="\"EVENTTYPE\"")
    private EventType eventType;

	@Column(name="\"STATE\"")
	private String state;

	@Column(name="\"TIMESTAMP\"")
	private long timestamp;

	@Column(name="TITLE")
	private String title;

	@Column(name="TRANSACTIONID")
	private String transactionid;

	@Column(name="\"USER\"")
	private String user;

	public PublishedEvent() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public EventType getEventType() {
        return eventType;
    }
	public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
	
	public String getSerializedform() {
		return this.serializedform;
	}

	public void setSerializedform(String serializedform) {
		this.serializedform = serializedform;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTransactionid() {
		return this.transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Consumed
	public void consumed() {
		setState("PROCESSED");
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getId();
	}
}