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

package com.mnxfst.basar.tracking.metric.pi;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.cache.CacheRoot;
import com.mnxfst.basar.tracking.cache.message.RegisterCacheWriterMessage;
import com.mnxfst.basar.tracking.cache.message.RegisterCacheWriterSuccessMessage;
import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.db.DatabaseRoot;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterSuccessMessage;
import com.mnxfst.basar.tracking.metric.Metric;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Tracks page impressions to a (visual) elements, eg. a web page or a in-app dialog.<br/><br/>
 * Processed event types: {@link TrackingEvent}<br/>
 * Required parameters:
 * <ul>
 *   <li><i>pv.src</i>: source the page impression event originates from</li>
 * </ul> 
 * Sub-Handlers:
 * <ul>
 *   <li><i>Page Impression Cache Handler</i>: writes all events to cache</li>
 *   <li><i>Page Impression Database Handler</i>: retrieves data from cache and updates the associated database element</li>
 * </ul> 
 * The cache handler as well as the database handler registration is delegated to the corresponding root nodes. 
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionHandler extends UntypedActor implements Metric {
	
	//////////////////////////////////////////////////////////////////////////////////////
	// CLASS VARIABLES --> PARAMETERS REQUIRED
	
	/** source the page impression event originate from a.k.a what did the user see */
	public static String PARAM_SOURCE = "pv.src";
	
	/** set of required parameters */
	public static Set<String> requiredParametes = new HashSet<>();	
	static {
		requiredParametes.add(PARAM_SOURCE);
	}
	
	//
	//////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////////////
	// INSTANCE VARIABLES 
	
	/** configuration of page impression handler */
	private final PageImpressionConfigElement piConfigElement;
	/** reference towards database root node */
	private final ActorRef databaseRootNodeRef;
	/** reference towards cache root node */
	private final ActorRef cacheRootNodeRef;
	/** reference to page impression database writer */
	private ActorRef piDatabaseWriterRef;
	/** reference to page imporession cache writer */
	private ActorRef piCacheWriterRef;
	
	//
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the handler using the provided input
	 * @param databaseRootNodeRef reference towards database root node required for registering the db writer 
	 * @param cacheRootNodeRef reference towards cache root node required for registering the cache writer
	 * @param basarTackingServerMetricConfigElement holds the configuration elements required for initializing this component
	 * TODO use central config element assigned to system for retrieving required nodes as these may increse in numbers in the future
	 */
	public PageImpressionHandler(final ActorRef databaseRootNodeRef, final ActorRef cacheRootNodeRef, final BasarTrackingServerMetricConfigElement basarTackingServerMetricConfigElement) {		
		this.piConfigElement = (PageImpressionConfigElement)basarTackingServerMetricConfigElement;
		this.databaseRootNodeRef = databaseRootNodeRef;
		this.cacheRootNodeRef = cacheRootNodeRef;
	}
	
	/**
	 * Register the database and cache components with the {@link DatabaseRoot database root node}/{@link CacheRoot cache root node} by
	 * simply sending an registration message
	 * @see akka.actor.UntypedActor#preStart()
	 */
	public void preStart() throws Exception {
		this.databaseRootNodeRef.tell(new RegisterDatabaseWriterMessage(piConfigElement.getDatabaseWriterId(), piConfigElement.getDatabaseWriterClass(), piConfigElement.getNumDatabaseWriters()), getSelf());
		this.cacheRootNodeRef.tell(new RegisterCacheWriterMessage(piConfigElement.getCacheWriterId(), piConfigElement.getCacheWriterClass(), piConfigElement.getNumCacheWriters()), getSelf());
		System.out.println("Send");
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof RegisterDatabaseWriterSuccessMessage) {
			// process messages received in response to database writer registration
			RegisterDatabaseWriterSuccessMessage msg = (RegisterDatabaseWriterSuccessMessage)message;
			this.piDatabaseWriterRef = msg.getComponentRef();			
		} else if(message instanceof RegisterCacheWriterSuccessMessage) {
			// process messages received in response to cache writer registration
			RegisterCacheWriterSuccessMessage msg = (RegisterCacheWriterSuccessMessage)message;
			this.piCacheWriterRef = msg.getComponentRef();
		} else if(message instanceof TrackingEvent) {
			// process treacking events
			TrackingEvent te = (TrackingEvent)message;
			te.getParameters().get(PARAM_SOURCE);
		} else {
			unhandled(message);
		}
		
	}

	/**
	 * @see com.mnxfst.basar.tracking.metric.Metric#getRequiredParameters()
	 */
	public Set<String> getRequiredParameters() {
		return PageImpressionHandler.requiredParametes;
	}

}
