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

package com.mnxfst.basar.tracking.actor.converter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.actor.msg.HttpRequestMessage;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Receives a {@link HttpRequestMessage} and converts it into a {@link TrackingEvent} 
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public final class HttpRequestConverter extends UntypedActor {

	private static final Logger logger = Logger.getLogger(HttpRequestConverter.class);
	
	public static final String REQ_PARAM_SOURCE = "ev.sr";
	public static final String REQ_PARAM_TYPE = "ev.tp";
	public static final String REQ_PARAM_INBOUND_INTERFACE = "ev.if";
	public static final String REQ_PARAM_TIMESTAMP = "ev.tm";

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// ensure that only expected message types are handled
		if(message instanceof HttpRequestMessage) {
			
			// convert and ensure that the contained request is not null ... obviously
			HttpRequestMessage reqMsg = (HttpRequestMessage)message;			
			if(reqMsg.getRequest() != null) {
				
				// get request and decode parameters
				HttpRequest request = reqMsg.getRequest();
				QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
				List<String> sources = queryStringDecoder.parameters().get(REQ_PARAM_SOURCE);
				List<String> types = queryStringDecoder.parameters().get(REQ_PARAM_TYPE);
				List<String> inboundInterfaces = queryStringDecoder.parameters().get(REQ_PARAM_INBOUND_INTERFACE);
				List<String> timestamps = queryStringDecoder.parameters().get(REQ_PARAM_TIMESTAMP);
				
				String timestampStr = (timestamps != null && !timestamps.isEmpty() ? timestamps.get(0) : "0");				
				long timestamp = System.currentTimeMillis();
				if(StringUtils.isNotBlank(timestampStr)) {
					try {
						timestamp = Long.valueOf(timestampStr); 
					} catch(Exception e) {
						logger.info("Failed to convert provided timestamp value '"+timestampStr+"' into numerical value. Error: " + e.getMessage());
					}
				}
				
				TrackingEvent trackingEvent = new TrackingEvent();
				trackingEvent.setInboundInterface((inboundInterfaces != null && !inboundInterfaces.isEmpty() ? inboundInterfaces.get(0) : "unknown"));
				trackingEvent.setSource((sources != null && !sources.isEmpty() ? sources.get(0) : "unknown"));
				trackingEvent.setType((types != null && !types.isEmpty() ? types.get(0) : "unknown"));
				trackingEvent.setTimestamp(timestamp);

				// transfer general request information
				trackingEvent.addParameter("method", request.getMethod().toString());
				trackingEvent.addParameter("protocol-version", request.getProtocolVersion().text());
				trackingEvent.addParameter("uri", request.getUri());

				// transfer header fields
				for(String headerName : request.headers().names()) {
					List<String> headerValues = request.headers().getAll(headerName);
					if(headerValues != null && !headerValues.isEmpty()) {
						for(String hv : headerValues)
							trackingEvent.addParameter(headerName, hv);
					} else {
						trackingEvent.addParameter(headerName, "");
					}
				}

				// transfer request parameters
				for(String reqParam : queryStringDecoder.parameters().keySet()) {
					List<String> reqParamValues = queryStringDecoder.parameters().get(reqParam);
					if(reqParamValues != null && !reqParamValues.isEmpty()) {
						for(Iterator<String> rpvIter = reqParamValues.iterator(); rpvIter.hasNext();) {
							trackingEvent.addParameter(reqParam, rpvIter.next());
						}
					} else {
						trackingEvent.addParameter(reqParam, "");
					}
				}

				// return converted object to sender
				getSender().tell(trackingEvent, getSelf());
			}
			
		} else {
			unhandled(message);
		}
	}
	
}
