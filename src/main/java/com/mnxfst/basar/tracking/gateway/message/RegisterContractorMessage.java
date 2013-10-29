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

package com.mnxfst.basar.tracking.gateway.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.mnxfst.basar.tracking.gateway.ContractorMessageGateway;
import com.mnxfst.basar.tracking.model.KPI;
import com.mnxfst.basar.tracking.model.Metric;

/**
 * Tells the {@link ContractorMessageGateway} to register a new contractor. The message
 * holds all required information to fully set up the contractor specific routing. Next to
 * the identifier used by the sender to mark events that belong to a specific contractor,
 * it holds a list of all metrics and KPIs relevant to that.  
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class RegisterContractorMessage implements Serializable {

	private static final long serialVersionUID = 5077336354989077867L;
	
	/** unique contractor key provided by each inbound event */
	private String contractorIdentifier = null;
	
	/** set of metrics to be tracked for the given contractor */
	private Set<Metric> metrics = new HashSet<>();
	
	/** set of KPIs to be derived for the given contractor */
	private Set<KPI> kpis = new HashSet<>();
	
	/**
	 * Default constructor
	 */
	public RegisterContractorMessage() {		
	}
	
	/**
	 * Initializes the message using the provided information
	 * @param contractorIdentifier
	 */
	public RegisterContractorMessage(final String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}

	public String getContractorIdentifier() {
		return contractorIdentifier;
	}

	public void setContractorIdentifier(String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}

	public Set<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(Set<Metric> metrics) {
		this.metrics = metrics;
	}

	public Set<KPI> getKpis() {
		return kpis;
	}

	public void setKpis(Set<KPI> kpis) {
		this.kpis = kpis;
	}

}
