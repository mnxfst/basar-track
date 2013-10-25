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

package com.mnxfst.basar.tracking.http.converter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.http.message.HttpRequestMessage;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Receives incoming {@link HttpRequestMessage http requests} and converts them into {@link TrackingEvent tracking events} 
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public final class HttpRequestConverter extends UntypedActor {

	/** logging facility */
	private static final Logger logger = Logger.getLogger(HttpRequestConverter.class);
	/** date formatted which converts a (current) time into the requested string format */
	private static final SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
	
	/** reference towards tracking event database root which receives all inbound event entities */
	private final ActorRef trackingEventDBRootRef;

	/** request parameter holding the event type */
	public static final String REQ_PARAM_TYPE = "ev.tp";
	/** request parameter holding the contractor */
	public static final String REQ_PARAM_CONTRACTOR = "ev.cr";
	/** request parameter holding the domain the event originates from */
	public static final String REQ_PARAM_DOMAIN = "ev.do";
	/** request parameter holding the source the event originates from */
	public static final String REQ_PARAM_SOURCE = "ev.sr";

	/**
	 * Initializes the request converter using the provided information
	 * @param trackingEventDBRootRef
	 */
	public HttpRequestConverter(final ActorRef trackingEventDBRootRef) {
		this.trackingEventDBRootRef = trackingEventDBRootRef;
	}
	
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
				List<String> contractors = queryStringDecoder.parameters().get(REQ_PARAM_CONTRACTOR);
				List<String> domains = queryStringDecoder.parameters().get(REQ_PARAM_DOMAIN);

				// prepare tracking event providing 'obvious' data
				TrackingEvent trackingEvent = new TrackingEvent();
				trackingEvent.setInboundInterface(reqMsg.getInboundInterface());
				trackingEvent.setSource((sources != null && !sources.isEmpty() ? sources.get(0) : "unknown"));
				trackingEvent.setType((types != null && !types.isEmpty() ? types.get(0) : "unknown"));
				trackingEvent.setTimestamp(sd.format(reqMsg.getTimestamp()));;
				trackingEvent.setContractor((contractors != null && !contractors.isEmpty() ? contractors.get(0) : ""));
				trackingEvent.setDomain((domains != null && !domains.isEmpty() ? domains.get(0) : ""));

				// transfer general request information
				trackingEvent.addParameter("method", (request.getMethod() != null ? request.getMethod().toString() : ""));
				trackingEvent.addParameter("protocol-version", (request.getProtocolVersion() != null ? request.getProtocolVersion().text() : ""));
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

				// send converted object to tracking event database root
				trackingEventDBRootRef.tell(trackingEvent, getSelf());
			}
			
		} else {
			unhandled(message);
		}
	}
	
}
