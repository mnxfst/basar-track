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

package com.mnxfst.basar.tracking.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

/**
 * Represents a metrics tracked by the application, eg. page impressions or basket size.
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class Metric implements Serializable {

	private static final long serialVersionUID = 8208050078260107787L;
	
	/** unique name of the metric */
	@NotNull
	@JsonProperty ( value = "name", required = true )	
	private String name = null;
	
	/** optional description of the metric */
	@JsonProperty ( value = "description" )
	private String description = null;
	
	/**
	 * Default constructor
	 */
	public Metric() {		
	}
	
	/**
	 * Initializes the metric using the provided information
	 * @param name
	 * @param description
	 */
	public Metric(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
