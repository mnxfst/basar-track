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

package com.mnxfst.basar.tracking.store;

import java.util.List;

import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoFactory;

/**
 * Root node to actor hierarchy reading/writing data from/to database. Provides access to
 * database and implements a {@link SupervisorStrategy supervisor strategy} to handle 
 * errors gracefully.
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class DatabaseRoot extends UntypedActor {

	private final MongoClient databaseClient;
	
	public DatabaseRoot(final List<String> databaseServers) {

		// database servers must be present
		if(databaseServers == null || databaseServers.isEmpty())
			throw new RuntimeException("Missing required database server destinations");
	
		// create configuration -- TODO add more fields
		MongoClientConfiguration databaseConfiguration = new MongoClientConfiguration();
		for(String serverAddress : databaseServers)
			databaseConfiguration.addServer(serverAddress);
				
		this.databaseClient = MongoFactory.createClient(databaseConfiguration);

		
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		// hell yeah :-)
	}

}
