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

package com.mnxfst.basar.tracking.metric.pi.config;

import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.metric.pi.cache.PageImpressionCacheWriter;
import com.mnxfst.basar.tracking.metric.pi.db.PageImpressionDBWriter;

/**
 * Provides all options required for setting up the page impression metric
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionConfigElement implements	BasarTrackingServerMetricConfigElement {
	
	private static final long serialVersionUID = -8144997631273667909L;
	
	private String identifier = null;
	private String metricClass = null;
	private String databaseWriterId = null;
	private String databaseWriterClass = PageImpressionDBWriter.class.getName();
	private String cacheWriterId = null;
	private String cacheWriterClass = PageImpressionCacheWriter.class.getName();
	private int numDatabaseWriters = 1;
	private int numCacheWriters = 1;
	
	public PageImpressionConfigElement() {		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getMetricClass() {
		return metricClass;
	}

	public void setMetricClass(String metricClass) {
		this.metricClass = metricClass;
	}

	public String getDatabaseWriterClass() {
		return databaseWriterClass;
	}

	public void setDatabaseWriterClass(String databaseWriterClass) {
		this.databaseWriterClass = databaseWriterClass;
	}

	public String getCacheWriterClass() {
		return cacheWriterClass;
	}

	public void setCacheWriterClass(String cacheWriterClass) {
		this.cacheWriterClass = cacheWriterClass;
	}

	public int getNumDatabaseWriters() {
		return numDatabaseWriters;
	}

	public void setNumDatabaseWriters(int numDatabaseWriters) {
		this.numDatabaseWriters = numDatabaseWriters;
	}

	public int getNumCacheWriters() {
		return numCacheWriters;
	}

	public void setNumCacheWriters(int numCacheWriters) {
		this.numCacheWriters = numCacheWriters;
	}

	public String getDatabaseWriterId() {
		return databaseWriterId;
	}

	public void setDatabaseWriterId(String databaseWriterId) {
		this.databaseWriterId = databaseWriterId;
	}

	public String getCacheWriterId() {
		return cacheWriterId;
	}

	public void setCacheWriterId(String cacheWriterId) {
		this.cacheWriterId = cacheWriterId;
	}
	
	

}
