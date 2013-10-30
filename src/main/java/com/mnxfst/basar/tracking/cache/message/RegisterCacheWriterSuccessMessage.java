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

package com.mnxfst.basar.tracking.cache.message;

import java.io.Serializable;

import akka.actor.ActorRef;

/**
 * Response to {@link RegisterCacheWriterMessage} providing the caller with the
 * {@link ActorRef component reference}
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class RegisterCacheWriterSuccessMessage implements Serializable {

	private static final long serialVersionUID = -2939906444454508165L;

	/** initially provided component identifier to give the receiver an opportunity to create a valid association */
	private String componentId = null;
	/** holds the reference towards the newly created database writer component */
	private ActorRef componentRef = null;
	
	/**
	 * Default constructor
	 */
	public RegisterCacheWriterSuccessMessage() {		
	}
	
	/**
	 * Initializes the message using the provided information
	 * @param componentId
	 * @param componentRef
	 */
	public RegisterCacheWriterSuccessMessage(final String componentId, final ActorRef componentRef) {
		this.componentId = componentId;
		this.componentRef = componentRef;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public ActorRef getComponentRef() {
		return componentRef;
	}

	public void setComponentRef(ActorRef componentRef) {
		this.componentRef = componentRef;
	}
}
