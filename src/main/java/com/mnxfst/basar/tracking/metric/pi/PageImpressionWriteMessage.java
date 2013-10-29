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

/**
 * Issued by {@link PageImpressionHandler} towards {@link PageImpressionCacheWriter} telling the writer to update the referenced
 * key by adding the provided value to the existing value. If none exists, the provided value is initial. <br/><br/>
 * The message is also sent to {@link PageImpressionDBWriter} telling the writer to overwrite any database contents referenced
 * by the (computed) key using the contained value 
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionWriteMessage implements Serializable {

	private static final long serialVersionUID = -2331441186482151458L;

	private String contractorIdentifier = null;
	private String pageImpressionSource = null;
	private int count = 0;
	private long timestamp = 0;
	
	/**
	 * Default constructor
	 */
	public PageImpressionWriteMessage() {		
	}
	
	/**
	 * Initializes the message using the provided input
	 * @param contractorIdentifier
	 * @param pageImpressionSource
	 * @param count
	 * @param timestamp
	 */
	public PageImpressionWriteMessage(final String contractorIdentifier, final String pageImpressionSource, final int count, final long timestamp) {
		this.contractorIdentifier = contractorIdentifier;
		this.pageImpressionSource = pageImpressionSource;
		this.count = count;
		this.timestamp = timestamp;
	}

	public String getContractorIdentifier() {
		return contractorIdentifier;
	}

	public void setContractorIdentifier(String contractorIdentifier) {
		this.contractorIdentifier = contractorIdentifier;
	}

	public String getPageImpressionSource() {
		return pageImpressionSource;
	}

	public void setPageImpressionSource(String pageImpressionSource) {
		this.pageImpressionSource = pageImpressionSource;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
