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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Core element of basar tracking server configuration
 * @author mnxfst
 * @since 25.10.2013
 *
 * Revision Control Info $Id$
 */
public class BasarTrackingServerConfiguration implements Serializable {

	private static final long serialVersionUID = -3310886944256863880L;
	
	/** application port. default: 8080 */
	private int port = 8080;
	
	/** known contractors - used to set up separate collection for storing events */ 
	private Set<String> contractors = new HashSet<>();

	/** name of the collection receiving all events that cannot be forwarded into a contractor specific collection */
	private String defaultTrackingEventCollection = null;
	
	/** list of hostname and ports of known database servers. format: <database host>:<port> */
	private List<String> databaseServers = new ArrayList<>();
	
	/** name of the database (inside mongodb) used for storing tracking events */
	private String databaseName = null;
	
	/** metrics configuration */
	private BasarTrackingServerMetricsConfigElement metrics = new BasarTrackingServerMetricsConfigElement();

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Set<String> getContractors() {
		return contractors;
	}

	public void setContractors(Set<String> contractors) {
		this.contractors = contractors;
	}

	public String getDefaultTrackingEventCollection() {
		return defaultTrackingEventCollection;
	}

	public void setDefaultTrackingEventCollection(
			String defaultTrackingEventCollection) {
		this.defaultTrackingEventCollection = defaultTrackingEventCollection;
	}

	public List<String> getDatabaseServers() {
		return databaseServers;
	}

	public void setDatabaseServers(List<String> databaseServers) {
		this.databaseServers = databaseServers;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @return the metrics
	 */
	public  BasarTrackingServerMetricsConfigElement getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(BasarTrackingServerMetricsConfigElement metrics) {
		this.metrics = metrics;
	}
	
	

}
