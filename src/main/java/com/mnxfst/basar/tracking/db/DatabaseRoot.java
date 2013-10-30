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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoFactory;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterErrorMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterSuccessMessage;

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
	private final MongoClient databaseClient;
	
	/** map of registered database writers */
	private final Map<String, ActorRef> registeredDatabaseWriters = new HashMap<>();	
	
	
	public DatabaseRoot(final List<String> databaseServers) {

		// database servers must be present
		if(databaseServers == null || databaseServers.isEmpty())
			throw new RuntimeException("Missing required database server destinations");
	
		// create configuration -- TODO add more fields
		MongoClientConfiguration databaseConfiguration = new MongoClientConfiguration();
		for(String serverAddress : databaseServers)
			databaseConfiguration.addServer(serverAddress);
				
		this.databaseClient = MongoFactory.createClient(databaseConfiguration);
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		if(message instanceof RegisterDatabaseWriterMessage)
			handleDatabaseRegistration((RegisterDatabaseWriterMessage)message);
		else
			unhandled(message);
	}
	
	/**
	 * Creates a new {@link DatabaseValueWriter database writer} instance from the provided
	 * input and informs the calling party about the new instance 
	 * @param msg
	 */
	protected void handleDatabaseRegistration(final RegisterDatabaseWriterMessage msg) {
		if(msg != null) {
			
			if(StringUtils.isBlank(msg.getComponentId())) {
				getSender().tell(new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Missing required component identifier", ERROR_MISSING_COMPONENT_IDENTIFIER), getSelf());
				return;
			}
			
			if(StringUtils.isBlank(msg.getComponentClass())) {
				getSender().tell(new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Missing required component class", ERROR_MISSING_COMPONENT_CLASS), getSelf());
				return;
			}
			
			if(msg.getNumOfInstances() < 1) {
				getSender().tell(new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Invalid number of instances: " + msg.getNumOfInstances(), ERROR_INVALID_NUMBER_OF_INSTANCES), getSelf());
				return;
			}
			
			// try to get the class referenced by the name contained in the message
			Class<?> dbWriterClass = null;
			try {
				dbWriterClass = Class.forName(msg.getComponentClass());
			} catch (ClassNotFoundException e) {
				getSender().tell(new RegisterDatabaseWriterErrorMessage(msg.getComponentId(), "Unknown component class: " + msg.getComponentClass(), ERROR_UNKNOWN_COMPONENT_CLASS), getSelf());
				return;
			}
			
			// instantiate actor 
			final ActorRef dbWriterRef;
			if(msg.getNumOfInstances() > 1)
				dbWriterRef = context().actorOf(Props.create(dbWriterClass, this.databaseClient).withRouter(new RoundRobinRouter(msg.getNumOfInstances())), msg.getComponentId());
			else
				dbWriterRef = context().actorOf(Props.create(dbWriterClass, this.databaseClient), msg.getComponentId());
			
			// register newly created database writer/actor with this actor
			this.registeredDatabaseWriters.put(msg.getComponentId(), dbWriterRef);
			
			getSender().tell(new RegisterDatabaseWriterSuccessMessage(msg.getComponentId(), dbWriterRef), getSelf());
		}		
	}
	
	
	
}
