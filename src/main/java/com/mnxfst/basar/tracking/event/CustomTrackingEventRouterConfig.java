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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import scala.collection.immutable.Seq;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.dispatch.Dispatchers;
import akka.routing.CustomRoute;
import akka.routing.CustomRouterConfig;
import akka.routing.Destination;
import akka.routing.RouteeProvider;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * @author mnxfst
 * @since 06.11.2013
 *
 * Revision $Id$
 */
public class CustomTrackingEventRouterConfig extends CustomRouterConfig {

	public static String DB_COLLECTION = "tevents";

	private final Set<String> contractorIds = new HashSet<>();
	private final Map<String, ActorRef> trackingEventWriters = new HashMap<>();
	private final MongoClient databaseClient;
	private ActorRef defaultTrackingEventWriterRef; 

	public CustomTrackingEventRouterConfig(final MongoClient databaseClient, final Set<String> contractorIds) {
		this.databaseClient = databaseClient;
		this.contractorIds.addAll(contractorIds);
	}
	
	/**
	 * @see akka.routing.RouterConfig#routerDispatcher()
	 */
	public String routerDispatcher() {
		return Dispatchers.DefaultDispatcherId();
	}

	/**
	 * @see akka.routing.RouterConfig#supervisorStrategy()
	 */
	public SupervisorStrategy supervisorStrategy() {
		return SupervisorStrategy.defaultStrategy();
	}

	/**
	 * @see akka.routing.CustomRouterConfig#createCustomRoute(akka.routing.RouteeProvider)
	 */
	public CustomRoute createCustomRoute(RouteeProvider routeeProvider) {
		
		for(String cid : contractorIds) {
			if(!trackingEventWriters.containsKey(cid)) {
				MongoCollection collection = this.databaseClient.getDatabase(cid).getCollection(DB_COLLECTION);
				this.trackingEventWriters.put(cid, routeeProvider.context().actorOf(Props.create(TrackingEventDBWriter.class, collection), "trackEventWriter-"+cid));
			}
		}
		
		if(defaultTrackingEventWriterRef == null) {
			MongoCollection collection = this.databaseClient.getDatabase("defdb").getCollection(DB_COLLECTION);
			defaultTrackingEventWriterRef = routeeProvider.context().actorOf(Props.create(TrackingEventDBWriter.class, collection), "trackEventWriter-default");
		}
		
		System.out.println("Accessing router code");
				
		return new CustomRoute() {
			
			/**
			 * @see akka.routing.CustomRoute#destinationsFor(akka.actor.ActorRef, java.lang.Object)
			 */
			public Seq<Destination> destinationsFor(ActorRef sender, Object message) {
				
				if(message instanceof TrackingEvent) {
					TrackingEvent te = (TrackingEvent)message;
					ActorRef destination = trackingEventWriters.get(te.getContractor());
					if(destination == null)
						destination = defaultTrackingEventWriterRef;
					
					return akka.japi.Util.immutableSingletonSeq(
					          new Destination(sender, destination));
				}
				
				return null;
			}
		};
	}
	
}
