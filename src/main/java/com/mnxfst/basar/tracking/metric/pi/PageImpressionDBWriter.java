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

import org.apache.commons.lang.StringUtils;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.builder.FindAndModify;
import com.mnxfst.basar.tracking.db.DatabaseValueWriter;

/**
 * Writes page impression values to database
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionDBWriter extends DatabaseValueWriter {

	public static final String DB_COLLECTION = "pi";
	
	public static final String DOC_FIELD_CONTRACTOR_ID = "ctr";
	public static final String DOC_FIELD_PI_SOURCE = "src";
	public static final String DOC_FIELD_COUNT = "cnt";
	public static final String DOC_FIELD_TIMESTAMP = "tsp";
	
	/**
	 * Initializes the instance using the provided information
	 * @param databaseClient
	 */
	public PageImpressionDBWriter(final MongoClient databaseClient) {
		super(databaseClient);
	}

	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// writes the contained message
		if(message instanceof PageImpressionWriteMessage) {
			updatePageImpression((PageImpressionWriteMessage)message);
		} else {
			unhandled(message);
		}
		
	}

	/**
	 * Updates the referenced pi element in database
	 * @param message
	 */
	protected void updatePageImpression(final PageImpressionWriteMessage message) {
		
		// allow updates only if the required information are available
		if(message != null && StringUtils.isNotBlank(message.getContractorIdentifier()) && StringUtils.isNotBlank(message.getPageImpressionSource()) && message.getCount() > 0) {

			MongoCollection collection = getCollection(message.getContractorIdentifier(), DB_COLLECTION);			
			DocumentBuilder updateBuilder = BuilderFactory.start();
			updateBuilder.add(DOC_FIELD_CONTRACTOR_ID, message.getContractorIdentifier());
			updateBuilder.add(DOC_FIELD_PI_SOURCE, message.getPageImpressionSource());
			updateBuilder.add(DOC_FIELD_COUNT, message.getCount());
			updateBuilder.add(DOC_FIELD_TIMESTAMP, message.getTimestamp()); // TODO use this value to check if the message contains the most current value
			
			DocumentBuilder queryBuilder = BuilderFactory.start();
			queryBuilder.add(DOC_FIELD_CONTRACTOR_ID, message.getContractorIdentifier());
			queryBuilder.add(DOC_FIELD_PI_SOURCE, message.getPageImpressionSource());
				
			FindAndModify findAndModifyTask = FindAndModify.builder().setQuery(queryBuilder).setUpdate(updateBuilder).build();
			collection.findAndModifyAsync(findAndModifyTask);
			
		}		
	}
	
}
