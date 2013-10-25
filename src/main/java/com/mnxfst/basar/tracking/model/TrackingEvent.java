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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Common model for representing all types events produced inside the basar context,
 * eg. tracking pixel, mobile in-app event, ... With respect to the open and extendible
 * nature of the platform there are only a few limitation concerning the fields stored
 * for each event. The core idea is simply to able to track everything produced now
 * or in the future. 
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public class TrackingEvent implements Serializable {

	private static final long serialVersionUID = -5172466917089438646L;
	
	/** event type */
	@JsonProperty ( value = "typ", required = true )
	private String type = null;
	/** interface that received this event */
	@JsonProperty ( value = "in", required = true )
	private String inboundInterface = null;
	/** describes the person or company that provides events */
	@JsonProperty ( value = "ctr", required = true )
	private String contractor = null;
	/** domain the event origins from, eg. webshop or mobile app */
	@JsonProperty ( value = "dom", required = true )
	private String domain = null;
	/** source of event which is a sub-element of domain, eg.  mobile site or specific app */
	@JsonProperty ( value = "src", required = true )
	private String source = null;
	/** time of event */
	@JsonProperty ( value = "time", required = true )
	private String timestamp = null;
	/** parameters specifying the the event */
	@JsonProperty ( value = "params", required = true )
	private Map<String, List<String>> parameters = new HashMap<>();
	
	/**
	 * Default constructor
	 */
	public TrackingEvent() {		
	}
	
	/**
	 * Initializes the event using the provided input
	 * @param type
	 * @param inboundInterface
	 * @param contractor
	 * @param domain
	 * @param source
	 * @param timestamp
	 */
	public TrackingEvent(final String type, final String inboundInterface, final String contractor, final String domain, final String source, final String timestamp) {
		this.type = type;
		this.inboundInterface = inboundInterface;
		this.contractor = contractor;
		this.domain = domain;
		this.source = source;
		this.timestamp = timestamp;
	}

	/**
	 * Adds the provided value to the parameter referenced by the key. The value
	 * will be added to the list which might hold more than one element
	 * @param key
	 * @param value
	 */
	public void addParameter(final String key, final String value) {
		List<String> values = this.parameters.get(key);
		if(values == null)
			values = new ArrayList<>();
		values.add(value);
		this.parameters.put(key, values);
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

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, List<String>> parameters) {
		this.parameters = parameters;
	}
	
}
