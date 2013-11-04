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

package com.mnxfst.basar.tracking.metric.pi;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

/**
 * Represents a page impression value for a given source
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpression implements Serializable {

	private static final long serialVersionUID = 7427720229518531945L;
	
	/** reference to contractor the value belongs to */
	@NotNull
	@JsonProperty ( value = "contractor", required = true )
	private String contractorIdentifier = null;
	
	/** element the pi is counted for */
	@NotNull
	@JsonProperty ( value = "source", required = true )
	private String source = null;
	
	/** impression count */
	@NotNull
	@JsonProperty ( value = "count", required = true )
	private long count = 0;

	/**
	 * Default constructor
	 */
	public PageImpression() {		
	}
	
	/**
	 * Initializes the instance using the provided information
	 * @param contractorIdentifier
	 * @param source
	 * @param count
	 */
	public PageImpression(final String contractorIdentifier, final String source, final long count) {
		this.contractorIdentifier = contractorIdentifier;
		this.source = source;
		this.count = count;
	}
	
	/**
	 * Adds the provided value to the current count
	 * @param value
	 */
	public void addCount(final long value) {
		this.count = this.count + value;
	}

	public String getContractorIdentifier() {
		return contractorIdentifier;
	}

	public void setContractorIdentifier(String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
