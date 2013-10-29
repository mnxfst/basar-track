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

package com.mnxfst.basar.tracking.event;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.json.Json;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnxfst.basar.tracking.model.TrackingEvent;
import com.mnxfst.basar.tracking.store.DatabaseValueWriter;

/**
 * Writes {@link TrackingEvent tracking events} to the previously configured database collection.
 * It uses the {@link MongoCollection#insertAsync(com.allanbank.mongodb.bson.DocumentAssignable...) asynchronous insertion method}
 * provided by the underlying data as no process is interested in a result/response.
 * @author mnxfst
 * @since 25.10.2013
 *
 * Revision Control Info $Id$
 */
public class TrackingEventDBWriter extends DatabaseValueWriter {

	public static String DB_COLLECTION = "tevents";
	
	/** mapper used to converting tracking events into their string representation */
	private final ObjectMapper trackingEventMapper = new ObjectMapper();
	
	/**
	 * Initializes the database writer using the provided input
	 * @param trackingEventCollection
	 */
	public TrackingEventDBWriter(final MongoClient databaseClient) {
		super(databaseClient);		
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// ensure that the message is of expected type, otherwise "mark" it as "not handled" 
		if(message instanceof TrackingEvent) {
			insertTrackingEvent((TrackingEvent)message);			
		} else {
			unhandled(message);
		}		
	}
	
	/**
	 * Writes the provided {@link TrackingEvent tracking event} to the configured {@link MongoCollection database collection}
	 * @param trackingEvent
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	protected void insertTrackingEvent(final TrackingEvent trackingEvent) throws JsonGenerationException, JsonMappingException, IOException {
		
		if(trackingEvent != null) {
			StringWriter stringWriter = new StringWriter();
			trackingEventMapper.writeValue(stringWriter, trackingEvent);
			String trackingEventString = stringWriter.toString();
			if(StringUtils.isNotBlank(trackingEventString)) {
				Document trackingEventDocument = Json.parse(trackingEventString);
				if(trackingEventDocument != null) {
					
					getCollection(trackingEvent.getContractor(), DB_COLLECTION).insertAsync(trackingEventDocument);
				}
			}
		}
	}

}
