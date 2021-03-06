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
 * Represents a KPI provided by the application, like conversion rate or average purchase per visit.
 * A KPI is derived from one or more {@link Metric metrics}.
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class KPI implements Serializable {

	private static final long serialVersionUID = 4106799382969917347L;

	/** unique name of the KPI */
	@NotNull
	@JsonProperty ( value = "name", required = true )	
	private String name = null;
	
	/** optional description of the KPI */
	@JsonProperty ( value = "description" )
	private String description = null;
	
	/**
	 * Default constructor
	 */
	public KPI() {		
	}
	
	/**
	 * Initializes the KPI using the provided information
	 * @param name
	 * @param description
	 */
	public KPI(final String name, final String description) {
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
