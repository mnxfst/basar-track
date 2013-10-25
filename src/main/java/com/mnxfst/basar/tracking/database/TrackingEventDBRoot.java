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

package com.mnxfst.basar.tracking.database;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Root node providing access to tracking event database. It sets up a number if {@link TrackingEventDBWriter tracking event writers}
 * each being assigned a different {@link MongoCollection database collection}, depending on the previously configured {@link TrackingEvent#getContractor() contractors}.<br/><br/>
 * To set up a single {@link TrackingEventDBWriter writer} for each contractor as long with a default writer receiving undeliverable events this
 * implementation uses a {@link TrackingEventDBRouterConfig customized router configuration} which analyzes each events {@link TrackingEvent#getContractor() contractor attribute} value. 
 * @author mnxfst
 * @since 25.10.2013
 *
 * Revision Control Info $Id$
 */
public class TrackingEventDBRoot extends UntypedActor {

	/** logging facility */
	private static final Logger logger = Logger.getLogger(TrackingEventDBRoot.class);
	
	private final List<String> databaseServers = new ArrayList<>();
	private final String databaseName;
	private final String defaultTrackingEventCollection;
	private final Set<String> contractors = new HashSet<>();
	
	private final MongoClient databaseClient;
//	private final ActorRef trackingEventDBWriterRef;
	private final Map<String, ActorRef> contractorEventWriterRefs = new HashMap<>();
	private final ActorRef defaultContractorEventWriterRef;

	/**
	 * Initializes the tracking event database root actor using the provided information
	 */
	public TrackingEventDBRoot(final List<String> databaseServers, final String databaseName, final String defaultTrackingEventCollection, final Set<String> contractors) {

		if(databaseServers == null || databaseServers.isEmpty())
			throw new RuntimeException("Missing required database server destinations");
		if(StringUtils.isBlank(defaultTrackingEventCollection))
			throw new RuntimeException("Missing required default collection for writing tracking events");
		
		this.databaseServers.addAll(databaseServers);
		this.databaseName = databaseName;
		this.defaultTrackingEventCollection = defaultTrackingEventCollection;
		if(contractors != null && !contractors.isEmpty())
			this.contractors.addAll(contractors);
		
		// create configuration -- TODO add more fields
		MongoClientConfiguration databaseConfiguration = new MongoClientConfiguration();
		for(String serverAddress : databaseServers)
			databaseConfiguration.addServer(serverAddress);
				
		this.databaseClient = MongoFactory.createClient(databaseConfiguration);
		MongoDatabase database = this.databaseClient.getDatabase(databaseName);

		// create a new routee for each contractor
		for(String contractor : contractors) {
			if(StringUtils.isNotBlank(contractor)) {
				contractorEventWriterRefs.put(contractor, context().actorOf(
						Props.create(TrackingEventDBWriter.class, database.getCollection(contractor))));
			}				
		}

		// create default contractor and add to map
		this.defaultContractorEventWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, database.getCollection(this.defaultTrackingEventCollection)));
		
		// TODO implement router configuration
//		this.trackingEventDBWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, databaseClient.getDatabase(databaseName).getCollection(defaultTrackingEventCollection))
//				.withRouter(
//						new TrackingEventDBRouterConfig(contractors, defaultTrackingEventCollection, database)), "trackingEventDBWriter");
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		// forward message to tracking event database writer
		if(message instanceof TrackingEvent) {
			
			final ActorRef eventWriterRef = this.contractorEventWriterRefs.get(((TrackingEvent)message).getContractor());
			logger.info(eventWriterRef);
			if(eventWriterRef != null)
				eventWriterRef.forward(message, context());
			else
				this.defaultContractorEventWriterRef.forward(message, context());
		} else {
			unhandled(message);
		}		
	}

}
