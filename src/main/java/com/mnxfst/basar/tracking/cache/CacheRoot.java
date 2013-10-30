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

package com.mnxfst.basar.tracking.cache;

import java.util.HashMap;
import java.util.Map;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.mnxfst.basar.tracking.cache.message.RegisterCacheWriterErrorMessage;
import com.mnxfst.basar.tracking.cache.message.RegisterCacheWriterMessage;
import com.mnxfst.basar.tracking.cache.message.RegisterCacheWriterSuccessMessage;

/**
 * Root node to actor hierarchy reading/writing data from/to cache. Provides access to
 * cache and implements a {@link SupervisorStrategy supervisor strategy} to handle 
 * errors gracefully.<br/><br/>
 * <b>Open issues</b>
 * <ul>
 *   <li>implement a supervisor strategy for failing cache connections</li>
 *   <li>derive a concept for multiple cache root nodes given the fact that writers are registered with the node instance creating them</li>
 *   <li>handle writer de-registration</li> 
 * </ul>
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class CacheRoot extends UntypedActor {

	///////////////////////////////////////////////////////////////////////////////////
	// error codes to be used when responding to invalid cache writer registrations
	public static final int ERROR_MISSING_COMPONENT_IDENTIFIER = 1;
	public static final int ERROR_MISSING_COMPONENT_CLASS = 2;
	public static final int ERROR_INVALID_NUMBER_OF_INSTANCES = 3;
	public static final int ERROR_UNKNOWN_COMPONENT_CLASS = 4;
	//
	///////////////////////////////////////////////////////////////////////////////////

	/** client used for accessing the memcached layer */
	private final MemcachedClient memcachedClient;
	
	/** map of registered cache writers */
	private final Map<String, ActorRef> registeredCacheWriters = new HashMap<>();	

	/**
	 * Initializes the cache root using the provided input
	 * @param memcachedClient
	 */
	public CacheRoot(final MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof RegisterCacheWriterMessage) {
			handleCacheWriterRegistration((RegisterCacheWriterMessage)message);
		} else {
			unhandled(message);
		}
		
	}

	/**
	 * Creates a new {@link CacheValueWriter cache writer} instance from the provided
	 * input and informs the calling party about the new instance 
	 * @param msg
	 */
	protected void handleCacheWriterRegistration(final RegisterCacheWriterMessage msg) {
		if(msg != null) {
			
			if(StringUtils.isBlank(msg.getComponentId())) {
				getSender().tell(new RegisterCacheWriterErrorMessage(msg.getComponentId(), "Missing required component identifier", ERROR_MISSING_COMPONENT_IDENTIFIER), getSelf());
				return;
			}
			
			if(StringUtils.isBlank(msg.getComponentClass())) {
				getSender().tell(new RegisterCacheWriterErrorMessage(msg.getComponentId(), "Missing required component class", ERROR_MISSING_COMPONENT_CLASS), getSelf());
				return;
			}
			
			if(msg.getNumOfInstances() < 1) {
				getSender().tell(new RegisterCacheWriterErrorMessage(msg.getComponentId(), "Invalid number of instances: " + msg.getNumOfInstances(), ERROR_INVALID_NUMBER_OF_INSTANCES), getSelf());
				return;
			}
			
			// try to get the class referenced by the name contained in the message
			Class<?> cacheWriterClass = null;
			try {
				cacheWriterClass = Class.forName(msg.getComponentClass());
			} catch (ClassNotFoundException e) {
				getSender().tell(new RegisterCacheWriterErrorMessage(msg.getComponentId(), "Unknown component class: " + msg.getComponentClass(), ERROR_UNKNOWN_COMPONENT_CLASS), getSelf());
				return;
			}
			
			// instantiate actor 
			final ActorRef cacheWriterRef;
			if(msg.getNumOfInstances() > 1)
				cacheWriterRef = context().actorOf(Props.create(cacheWriterClass, this.memcachedClient).withRouter(new RoundRobinRouter(msg.getNumOfInstances())), msg.getComponentId());
			else
				cacheWriterRef = context().actorOf(Props.create(cacheWriterClass, this.memcachedClient), msg.getComponentId());
			
			// register newly created cache writer/actor with this actor
			this.registeredCacheWriters.put(msg.getComponentId(), cacheWriterRef);
			
			getSender().tell(new RegisterCacheWriterSuccessMessage(msg.getComponentId(), cacheWriterRef), getSelf());
		}		
	}
	
}
