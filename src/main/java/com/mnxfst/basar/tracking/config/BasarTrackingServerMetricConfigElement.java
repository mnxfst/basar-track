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

package com.mnxfst.basar.tracking.config;

import java.io.Serializable;

/**
 * Common interface for configuring a single metric
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public interface BasarTrackingServerMetricConfigElement extends Serializable {

	/** set unique metric identifier */
	public void setIdentifier(String identifier);
	/** get unique metric identifier */
	public String getIdentifier();
	/** set metric class */
	public void setMetricClass(String metricClass);
	/** get metric class */
	public String getMetricClass();
	/** set metric database writer class */
	public void setDatabaseWriterClass(String databaseWriterClass);
	/** get metric database writer class */
	public String getDatabaseWriterClass();
	/** set metric cache writer class */
	public void setCacheWriterClass(String cacheWriterClass);
	/** get metric cache writer class */
	public String getCacheWriterClass();
	/** set the number of database writers to be used for persisting the implementing metric */
	public void setNumDatabaseWriters(int numDatabaseWriters);
	/** get the number of database writers to be used for persisting the implementing metric */
	public int getNumDatabaseWriters();
	/** set the number of cache writers */
	public void setNumCacheWriters(int numCacheWriters);
	/** get the number of cache writers */
	public int getNumCacheWriters();

}
