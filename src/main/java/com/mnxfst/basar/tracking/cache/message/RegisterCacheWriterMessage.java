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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mnxfst.basar.tracking.cache.CacheRoot;
import com.mnxfst.basar.tracking.cache.CacheValueWriter;

/**
 * Requests the {@link CacheRoot cache root node} to register a new {@link CacheValueWriter cache value writer}.
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class RegisterCacheWriterMessage implements Serializable {

	private static final long serialVersionUID = 2552944926243570970L;
	
	/** unique key used as identifier when creating the writer component */  
	@JsonProperty ( value = "componentId", required = true )
	private String componentId = null;

	/** component class */
	@JsonProperty ( value = "componentClass", required = true )
	private String componentClass = null;
	
	/** number of instances to create and provide through round-robin-router */
	@JsonProperty ( value = "numInstances", required = true )
	private int numOfInstances = 1;
	
	/**
	 * Default constructor
	 */
	public RegisterCacheWriterMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param componentId
	 * @param componentClass
	 * @param numOfInstances
	 */
	public RegisterCacheWriterMessage(final String componentId, final String componentClass, final int numOfInstances) {
		this.componentId = componentId;
		this.componentClass = componentClass;
		this.numOfInstances = numOfInstances;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentClass() {
		return componentClass;
	}

	public void setComponentClass(String componentClass) {
		this.componentClass = componentClass;
	}

	public int getNumOfInstances() {
		return numOfInstances;
	}

	public void setNumOfInstances(int numOfInstances) {
		this.numOfInstances = numOfInstances;
	}

}
