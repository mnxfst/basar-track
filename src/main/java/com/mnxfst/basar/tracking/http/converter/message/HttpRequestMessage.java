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

package com.mnxfst.basar.tracking.http.converter.message;

import io.netty.handler.codec.http.HttpRequest;

import java.io.Serializable;

/**
 * Holds an inbound {@link HttpRequest http request} for further processing. The
 * request has been mapped into a wrapper to easily extend this message type
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public class HttpRequestMessage implements Serializable {

	private static final long serialVersionUID = -1421832192192393860L;
	
	private HttpRequest request = null;
	private String inboundInterface = null;
	private long timestamp = 0;
	
	public HttpRequestMessage() {		
	}
	
	public HttpRequestMessage(HttpRequest request, String inboundInterface, long timestamp) {
		this.request = request;
		this.inboundInterface = inboundInterface;
		this.timestamp = timestamp;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public String getInboundInterface() {
		return inboundInterface;
	}

	public void setInboundInterface(String inboundInterface) {
		this.inboundInterface = inboundInterface;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
