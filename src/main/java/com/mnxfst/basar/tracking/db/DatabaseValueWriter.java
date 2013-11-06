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

package com.mnxfst.basar.tracking.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;

/**
 * Common parent to all nodes that wish to write data to the underlying database. This implementation provides
 * an expected constructor interface that all extending classes must comply with. Although other nodes are
 * allowed to access the database as well children of a common database root share the same {@link SupervisorStrategy supervisor strategy}
 * they benefit from. 
 *   
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public abstract class DatabaseValueWriter extends UntypedActor {

	/** database client */
	private final MongoClient databaseClient;
	/** references to already accessed databases .. simple cache */
	private final Map<String, MongoDatabase> databases = new HashMap<>();
	
	/**
	 * Initializes the database writer using the provided input
	 * @param databaseClient
	 */
	public DatabaseValueWriter(final MongoClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	/**
	 * Retrieves the {@link MongoDatabase database} assigned to the referenced contractor
	 * @param contractorIdentifier
	 * @return returns the contractors database if one exists otherwise it returns null
	 */
	protected MongoDatabase getDatabase(final String contractorIdentifier) {
		if(StringUtils.isNotBlank(contractorIdentifier)) {
			MongoDatabase contractorDatabase = this.databases.get(contractorIdentifier);
			if(contractorDatabase == null) {
				contractorDatabase = this.databaseClient.getDatabase(contractorIdentifier);
				this.databases.put(contractorIdentifier, contractorDatabase);
				return contractorDatabase;
			} else {
				return contractorDatabase;
			}
		}		
		return null;
	}
	
	/**
	 * Returns the named collection belonging to the database of the given contractor 
	 * @param contractorIdentifier
	 * @param collectionName 
	 * @return referenced collection if it exists otherwise it returns null
	 */
	protected MongoCollection getCollection(final String contractorIdentifier, final String collectionName) {
		MongoDatabase contractorDatabase = getDatabase(contractorIdentifier);
		if(contractorDatabase != null) {
			return contractorDatabase.getCollection(collectionName);
		}
		
		return null;		
	}
}
