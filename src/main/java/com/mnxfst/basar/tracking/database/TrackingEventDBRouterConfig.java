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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import scala.collection.immutable.Seq;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.dispatch.Dispatchers;
import akka.japi.Util;
import akka.routing.CustomRoute;
import akka.routing.CustomRouterConfig;
import akka.routing.Destination;
import akka.routing.RouteeProvider;

import com.allanbank.mongodb.MongoDatabase;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Customized router configuration that ensures {@link TrackingEvent tracking events} to be
 * forwarded to the {@link TrackingEventDBWriter database writer} which is in charge for it.
 * Which one is assigned with an incoming event depends on the data found in the 
 * {@link TrackingEvent#getContractor() contractor field}.<br/><br/>
 * The use of the contractor to differentiate between database collections is just due to
 * the initial state of this implementation.  <br/><br/>
 * For more information on the implementation of custom router configrations, please see {@linkplain http://doc.akka.io/docs/akka/2.2.2/java/routing.html}
 * @author mnxfst
 * @since 25.10.2013
 *
 * Revision Control Info $Id$
 */
public class TrackingEventDBRouterConfig extends CustomRouterConfig {

	/** logging facility */
	private static final Logger logger = Logger.getLogger(TrackingEventDBRouterConfig.class);
	
	/** contractors known to provided events */
	private final Set<String> contractors;
	/** name of the collection receiving events in case the contractor cannot be determined or found */
	private final String defaultTrackingEventCollection;
	/** reference towards the underyling database */
	private final MongoDatabase database;
	
	/**
	 * Initializes the router configuration using the provided input
	 * @param contractors
	 * @param defaultTrackingEventCollection
	 * @param database
	 */
	public TrackingEventDBRouterConfig(final Set<String> contractors, final String defaultTrackingEventCollection, final MongoDatabase database) {
		logger.info("instance: " + this);
		this.contractors = contractors;
		this.defaultTrackingEventCollection = defaultTrackingEventCollection;
		this.database = database;
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
		
		// create a new routee for each contractor
		final Map<String, ActorRef> contractorEventWriterRefs = new HashMap<>();
		for(String contractor : contractors) {
			if(StringUtils.isNotBlank(contractor)) {
				contractorEventWriterRefs.put(contractor, 
						routeeProvider.context().actorOf(
								Props.create(TrackingEventDBWriter.class, this.database.getCollection(contractor))
						)
				);
			}				
		}

		// create default contractor and add to map
		final ActorRef defaultContractorEventWriterRef = routeeProvider.context().actorOf(
				Props.create(TrackingEventDBWriter.class, this.database.getCollection(this.defaultTrackingEventCollection)));
		contractorEventWriterRefs.put(defaultTrackingEventCollection, defaultContractorEventWriterRef);
		
		// registers routees with provider <<--- required, otherwise an ActorInitializationException is thrown
		routeeProvider.registerRoutees(contractorEventWriterRefs.values());
		
		logger.info("Created");
		/**
		 * Create a custom route for each contractor 
		 */
		return new CustomRoute() {
			
			/**
			 * @see akka.routing.CustomRoute#destinationsFor(akka.actor.ActorRef, java.lang.Object)
			 */
			public Seq<Destination> destinationsFor(ActorRef sender, Object message) {

				logger.info("msg: " + message);
				// convert message, extract contractor, lookup writer and create route
				TrackingEvent event = (TrackingEvent)message;
				ActorRef eventWriterRef = contractorEventWriterRefs.get(event.getContractor());
				if(eventWriterRef != null) {
					logger.info("trackingEventRouter[contractor="+event.getContractor()+", default=false, writer="+eventWriterRef.path()+"]");
					return Util.immutableSeq(new Destination(sender, eventWriterRef));
				}

				// otherwise: simply forward it to default handler
				logger.info("trackingEventRouter[contractor="+event.getContractor()+", default=true, writer="+defaultContractorEventWriterRef.path()+"]");
				return Util.immutableSeq(new Destination(sender, defaultContractorEventWriterRef));
			}
		};		
	}

}
