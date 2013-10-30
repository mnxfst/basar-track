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

/**
 * Notifies the node {@link RegisterCacheWriterMessage requesting} the registration of a new database
 * writer about an error while doing so.
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class RegisterCacheWriterErrorMessage implements Serializable {
		
	private static final long serialVersionUID = -5573663454285681665L;
	
	
	/** initially provided component identifier */
	@JsonProperty ( value = "componentId", required = true )
	private String componentId = null;
	/** error message */
	@JsonProperty ( value = "errorMessage", required = true )
	private String errorMessage = null;
	/** error code */
	@JsonProperty ( value = "code", required = true )
	private int code = 0;
	
	/**
	 * Default constructor
	 */
	public RegisterCacheWriterErrorMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param componentId
	 * @param errorMessage
	 * @param errorCode
	 */
	public RegisterCacheWriterErrorMessage(final String componentId, final String errorMessage, final int errorCode) {
		this.componentId = componentId;
		this.errorMessage = errorMessage;
		this.code = errorCode;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
