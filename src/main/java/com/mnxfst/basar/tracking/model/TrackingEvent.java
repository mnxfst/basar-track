/**
 * basar - enhanced electronic marketplace
 * Copyright (C) 2013 Christian Kreutzfeldt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mnxfst.basar.tracking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common model used for representing all tracked events/event types regardless of the 
 * producing source, eg. mobile app, tracking pixel ...
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public class TrackingEvent implements Serializable {

	private static final long serialVersionUID = -5172466917089438646L;

	/** time of event */
	private long timestamp = 0;
	
	/** source of event, eg. mobile app, tracking pixel ... */
	private String source = null;
	
	/** type of event, eg. movie event, customer tracking, mobile app event ... */
	private String type = null;
	
	/** interface used to supply tracking server with event */
	private String inboundInterface = null;
	
	/** event parameters, eg. referer, starting frame, id of clicked button */
	private Map<String, List<Serializable>> parameters = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public TrackingEvent() {		
	}
	
	/**
	 * Initializes the event using the provided information
	 * @param timestamp
	 * @param source
	 * @param type
	 * @param inboundInterface
	 */
	public TrackingEvent(long timestamp, String source, String type, String inboundInterface) {
		this.timestamp = timestamp;
		this.source = source;
		this.type = type;
		this.inboundInterface = inboundInterface;
	}
	
	/**
	 * Creates a new parameter or appends the provided value to the list
	 * of existing parameter.
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, Serializable value) {
		
		List<Serializable> values = this.parameters.get(key);
		if(values == null)
			values = new ArrayList<>();
		values.add(value);
		this.parameters.put(key, values);		
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInboundInterface() {
		return inboundInterface;
	}

	public void setInboundInterface(String inboundInterface) {
		this.inboundInterface = inboundInterface;
	}

	public Map<String, List<Serializable>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, List<Serializable>> parameters) {
		this.parameters = parameters;
	}
	
	
}
