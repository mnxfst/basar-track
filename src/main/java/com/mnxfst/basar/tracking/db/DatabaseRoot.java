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

package com.mnxfst.basar.tracking.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoFactory;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterErrorMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterSuccessMessage;
import com.mnxfst.basar.tracking.event.CustomTrackingEventRouterConfig;
import com.mnxfst.basar.tracking.event.TrackingEventDBWriter;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Root node to actor hierarchy reading/writing data from/to database. Provides access to
 * database and implements a {@link SupervisorStrategy supervisor strategy} to handle 
 * errors gracefully.<br/><br/>
 * <b>Open issues</b>
 * <ul>
 *   <li>implement a supervisor strategy for failing database connections</li>
 *   <li>derive a concept for multiple database root nodes given the fact that writers are registered with the node instance creating them</li>
 *   <li>handle writer de-registration</li> 
 * </ul>
 * @author mnxfst
 * @since 30.10.2013
 * Revision Control Info $Id$
 */
public class DatabaseRoot extends UntypedActor {

	
	///////////////////////////////////////////////////////////////////////////////////
	// error codes to be used when responding to invalid database writer registrations
	public static final int ERROR_MISSING_COMPONENT_IDENTIFIER = 1;
	public static final int ERROR_MISSING_COMPONENT_CLASS = 2;
	public static final int ERROR_INVALID_NUMBER_OF_INSTANCES = 3;
	public static final int ERROR_UNKNOWN_COMPONENT_CLASS = 4;
	//
	///////////////////////////////////////////////////////////////////////////////////

	/** reference towards client required for accessing the mongodb */ 
	private MongoClient databaseClient;
	
	/** map of registered database writers */
	private final Map<String, ActorRef> registeredDatabaseWriters = new HashMap<>();
	
	/** mongodb client configuration */
	private final MongoClientConfiguration databaseClientConfiguration;
	
	/** number of tracking event writers: default 1 */
	private int numOfTrackingEventWriters = 1;
	
	/** reference towards tracking event writer(s) */
	private ActorRef trackingEventWriterRef = null;
	
	/**
	 * Initializes the instance using the provided input. The constructor does not establish a connection
	 * with the referenced database servers as this will be carried out by preStart()
	 * @param databaseServers
	 */
	public DatabaseRoot(final List<String> databaseServers, final int numOfTrackingEventWriters) {

		// database servers must be present
		if(databaseServers == null || databaseServers.isEmpty())
			throw new RuntimeException("Missing required database server destinations");
	
		if(numOfTrackingEventWriters > 1)
			this.numOfTrackingEventWriters = numOfTrackingEventWriters;
		
		// create configuration -- TODO add more fields
		this.databaseClientConfiguration = new MongoClientConfiguration();
		for(String serverAddress : databaseServers)
			databaseClientConfiguration.addServer(serverAddress);
	}
	
	/**
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		this.databaseClient = MongoFactory.createClient(this.databaseClientConfiguration);

		Set<String> contractorIds = new HashSet<>();
		contractorIds.add("contractor2");
		
		MongoCollection collection = this.databaseClient.getDatabase("defdb").getCollection(CustomTrackingEventRouterConfig.DB_COLLECTION);
//		defaultTrackingEventWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, collection), "trackEventWriter-default");

		this.trackingEventWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, collection)
				.withRouter(new CustomTrackingEventRouterConfig(databaseClient, contractorIds)));
		
//		if(this.numOfTrackingEventWriters > 1)
//			this.trackingEventWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, this.databaseClient).
//					withRouter(new RoundRobinRouter(this.numOfTrackingEventWriters)), "trackingEventWriter");
//		else
//			this.trackingEventWriterRef = context().actorOf(Props.create(TrackingEventDBWriter.class, this.databaseClient), "trackingEventWriter");		
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		if(message instanceof RegisterDatabaseWriterMessage) {
			Serializable responseMessage = handleDatabaseRegistration((RegisterDatabaseWriterMessage)message);
			if(responseMessage != null) {
				getSender().tell(responseMessage, getSelf());
			}
		} else if(message instanceof TrackingEvent) {
			this.trackingEventWriterRef.tell(message, getSelf()); // TODO implement router for sending content to different collections
		} else {
			unhandled(message);
		}
		
		
	}
	
	/**
	 * Creates a new {@link DatabaseValueWriter database writer} instance from the provided
	 * input and informs the calling party about the new instance 
	 * @param msg
	 */
	protected Serializable handleDatabaseRegistration(final RegisterDatabaseWriterMessage msg) {
		
		// ensure that the received message is not null ... which shouldn't be the case the onReceive ensured the instance
		if(msg != null) {
			
			////////////////////////////////////////////////////////////////////////////
			// validate the received message
			
			// the component identifier is required as it is used for accessing the writer directly
			if(StringUtils.isBlank(msg.getComponentId())) {
				return new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Missing required component identifier", ERROR_MISSING_COMPONENT_IDENTIFIER);
				
			}
			
			// the component class must not be blank as it is required when creating the instance 
			if(StringUtils.isBlank(msg.getComponentClass())) {
				return new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Missing required component class", ERROR_MISSING_COMPONENT_CLASS);
			}
			
			// the number of instances to be created must be at least 1 ... obviously ;-)
			if(msg.getNumOfInstances() < 1) {
				return new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Invalid number of instances: " + msg.getNumOfInstances(), ERROR_INVALID_NUMBER_OF_INSTANCES);
			}
			
			// try to get the class referenced by the name contained in the message
			Class<?> dbWriterClass = null;
			try {
				dbWriterClass = Class.forName(msg.getComponentClass());
			} catch (ClassNotFoundException e) {
				return new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Unknown component class: " + msg.getComponentClass(), ERROR_UNKNOWN_COMPONENT_CLASS);
			}

			// instantiate actor 
			final ActorRef dbWriterRef;
			if(msg.getNumOfInstances() > 1)
				dbWriterRef = context().actorOf(Props.create(dbWriterClass, this.databaseClient).withRouter(new RoundRobinRouter(msg.getNumOfInstances())), msg.getComponentId());
			else
				dbWriterRef = context().actorOf(Props.create(dbWriterClass, this.databaseClient), msg.getComponentId());
			
			// register newly created database writer/actor with this actor and tell the sender about it
			this.registeredDatabaseWriters.put(msg.getComponentId(), dbWriterRef);
			context().system().log().info("databaseWriter[class="+msg.getComponentClass()+", numInstances="+msg.getNumOfInstances()+", componentId="+msg.getComponentId()+", path="+dbWriterRef.path()+"]");

			return new RegisterDatabaseWriterSuccessMessage(msg.getComponentId(), dbWriterRef);
		}
		
		return null;
	}

	/**
	 * @return the registeredDatabaseWriters
	 */
	protected Map<String, ActorRef> getRegisteredDatabaseWriters() {
		return registeredDatabaseWriters;
	}
	
}
