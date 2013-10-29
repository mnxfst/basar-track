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
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage;
import com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage;

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
	
	private Map<String, ActorRef> metricHandlers = new HashMap<>();
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
	}

	protected void initMetricHandlers(final List<BasarTrackingServerMetricConfigElement> metricConfigs) {
		
		
		
	}
	
}
