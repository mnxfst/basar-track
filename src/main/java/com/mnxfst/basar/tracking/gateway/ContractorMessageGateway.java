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

package com.mnxfst.basar.tracking.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage;
import com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage;
import com.mnxfst.basar.tracking.model.Metric;
import com.mnxfst.basar.tracking.model.TrackingEvent;

/**
 * Instances of the contractor message gateway are the root nodes to receive all 
 * inbound traffic which originates from any contractor. The actor fetches the
 * KPI configuration from for the referenced contractor and forwards the message
 * to the specific nodes. These in turn decide on how to deal with it.<br/><br/>
 * The contractor processes these message types:
 * <ul>
 *   <li><b>{@link RegisterContractorMessage}</b>: register a new contractor along with its routing information</li>
 *   <li><b>{@link DeregisterContractorMessage}</b>: de-registers an existing contractor</li> 
 * </ul>   
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class ContractorMessageGateway extends UntypedActor {
	
	/** map of metric identifiers towards the actors handling inbound messages for that type */ 
	private Map<String, ActorRef> metricHandlers = new HashMap<>();
	
	/** map of contractor identifiers towards metrics they are registered for */
	private Map<String, Set<String>> contractorMetrics = new HashMap<>();
	
	/** reference towards database root node */
	private final ActorRef databaseRootNodeRef;
	/** reference towards cache root node */
	private final ActorRef cacheRootNodeRef;

	/**
	 * Initializes the gateway using the provided input
	 * @param databaseRootNodeRef
	 * @param cacheRootNodeRef
	 * TODO remove the references towards the database / cache root nodes and replace it by a lookup
	 */
	public ContractorMessageGateway(final ActorRef databaseRootNodeRef, final ActorRef cacheRootNodeRef) {
		this.databaseRootNodeRef = databaseRootNodeRef;
		this.cacheRootNodeRef = cacheRootNodeRef;
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// tell database node about message
		if(message instanceof TrackingEvent) {
			databaseRootNodeRef.tell(message, getSelf());
			
			TrackingEvent te = (TrackingEvent)message;
			String contractor = te.getContractor();
			Set<String> coMetrics = this.contractorMetrics.get(contractor);
			if(coMetrics != null && !coMetrics.isEmpty()) {
				for(String cm : coMetrics) {
					System.out.println("cm: " + cm);
					ActorRef metricHandler = metricHandlers.get(cm);
					System.out.println("handler: " + metricHandler);
					if(metricHandler != null)
						metricHandler.tell(te, getSelf());
				}
			}
		} else if(message instanceof RegisterContractorMessage) {
			registerContractor((RegisterContractorMessage)message); // TODO response?
		} else if(message instanceof BasarTrackingServerMetricConfigElement) {
			initMetricHandler((BasarTrackingServerMetricConfigElement)message);
		}
		
	}

	/**
	 * Registers a new contractor with the message gateway
	 * @param message
	 */
	protected void registerContractor(final RegisterContractorMessage message) {
		
		if(message != null) {

			if(StringUtils.isNotBlank(message.getContractorIdentifier())) {

				if(message.getMetrics() != null && !message.getMetrics().isEmpty()) {

					// fetch the set of metrics the current contractor is already registered for ... if no such set exists: create one
					Set<String> registeredMetrics = this.contractorMetrics.get(message.getContractorIdentifier());
					if(registeredMetrics == null)
						registeredMetrics = new HashSet<>();
					
					// iterate through provided metrics, check if a handler exists and register the contractor for it
					for(Metric metric : message.getMetrics()) {
						if(StringUtils.isNotBlank(metric.getName())) {
							if(this.metricHandlers.containsKey(metric.getName())) {
								registeredMetrics.add(metric.getName());
								context().system().log().info("successfully registered contractor [id="+message.getContractorIdentifier()+", metric="+metric.getName()+"]");
							} else {
								context().system().log().error("failed to register contractor '"+message.getContractorIdentifier()+"' for '"+metric.getName()+"': no such metric");
							}
						}
					}
					
					if(registeredMetrics != null && !registeredMetrics.isEmpty())
						this.contractorMetrics.put(message.getContractorIdentifier(), registeredMetrics);					
				}

				if(message.getKpis() != null && !message.getKpis().isEmpty()) {
					// TODO 
				}
				
			} else {
				context().system().log().error("message contractor registration failed: missing required contractor identifier");
			}
		}
	}
	
	/**
	 * Remove contractor from gateway
	 * @param message
	 */
	protected void deregisterContractor(final DeregisterContractorMessage message) {
		
		if(message != null) {
			if(StringUtils.isNotBlank(message.getContractorIdentifier())) {				
				Set<String> registeredMetrics = this.contractorMetrics.get(message.getContractorIdentifier());
				if(registeredMetrics != null && !registeredMetrics.isEmpty())
					registeredMetrics.clear();
				this.contractorMetrics.remove(message.getContractorIdentifier());
			} else {
				context().system().log().error("message contractor deregistration failed: missing required contractor identifier");
			}
		}		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// METRIC HANDLER INITIALIZATION 
	
	/**
	 * Reads out metric configuration and provides it to {@link ContractorMessageGateway#initMetricHandler(BasarTrackingServerMetricConfigElement)} 
	 * for further processing
	 * @param metricConfigs
	 */
	protected void initMetricHandlers(final List<BasarTrackingServerMetricConfigElement> metricConfigs) {
		
		if(metricConfigs != null && !metricConfigs.isEmpty()) {
		
			// step through list of config elements and create a new actor from each
			for(BasarTrackingServerMetricConfigElement cfgElement : metricConfigs) {
				initMetricHandler(cfgElement);
			}			
		} else {
			context().system().log().info("No metrics configuration received");
		}		
	}

	/**
	 * Reads out metric configuration, initializes a new actor and registers it with the 
	 * {@link ContractorMessageGateway#metricHandlers metrics handler map}
	 * @param cfgElement
	 */
	protected void initMetricHandler(final BasarTrackingServerMetricConfigElement cfgElement) {
		// ensure the current element is valid and holds valid values
		if(cfgElement != null) {
			if(StringUtils.isNotBlank(cfgElement.getIdentifier()) && StringUtils.isNotBlank(cfgElement.getMetricClass())) {
			
				try {
					this.metricHandlers.put(cfgElement.getIdentifier(), instantiateMetricHandler(cfgElement));							
					context().system().log().info("metric handler successfully initialized: [id="+cfgElement.getIdentifier()+", class="+cfgElement.getMetricClass()+"]");
				} catch(ClassNotFoundException e) {
					context().system().log().error("metric handler class not found: [id="+cfgElement.getIdentifier()+", class="+cfgElement.getMetricClass()+"]");
				}
			} else {
				context().system().log().error("invalid metric handler configuration: [id="+cfgElement.getIdentifier()+", class="+cfgElement.getMetricClass()+"]");
			}
		} else {
			context().system().log().error("invalid metric handler configuration: null");
		}
	}
	
	/**
	 * Initializes a new metric handler actor using the provided configuration
	 * @param metricCfgElement
	 * @return
	 * @throws ClassNotFoundException 
	 */
	protected ActorRef instantiateMetricHandler(final BasarTrackingServerMetricConfigElement metricCfgElement) throws ClassNotFoundException {
		Class<?> metricsHandlerClass = Class.forName(metricCfgElement.getMetricClass());
		return context().actorOf(Props.create(metricsHandlerClass, this.databaseRootNodeRef, this.cacheRootNodeRef, metricCfgElement), metricCfgElement.getIdentifier());		
	}
	
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the metricHandlers
	 */
	protected Map<String, ActorRef> getMetricHandlers() {
		return metricHandlers;
	}

	/**
	 * @return the contractorMetrics
	 */
	protected Map<String, Set<String>> getContractorMetrics() {
		return contractorMetrics;
	}
	
}
 