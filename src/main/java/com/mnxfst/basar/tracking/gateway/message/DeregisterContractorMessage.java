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

import com.mnxfst.basar.tracking.gateway.ContractorMessageGateway;

/**
 * Tells the {@link ContractorMessageGateway} to remove all routing information for 
 * the reference contractor from gateway
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class DeregisterContractorMessage implements Serializable {

	private static final long serialVersionUID = 7153488755957572497L;

	/** unique contractor key provided by each inbound event */
	private String contractorIdentifier = null;

	/**
	 * Default constructor
	 */
	public DeregisterContractorMessage() {		
	}
	
	/**
	 * Initializes the message using the provided information
	 * @param contractorIdentifier
	 */
	public DeregisterContractorMessage(final String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}

	public String getContractorIdentifier() {
		return contractorIdentifier;
	}

	public void setContractorIdentifier(String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}
}
