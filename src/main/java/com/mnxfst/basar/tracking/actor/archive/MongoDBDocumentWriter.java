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

package com.mnxfst.basar.tracking.actor.archive;

import java.io.Serializable;

import scala.concurrent.duration.Duration;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;

import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.mnxfst.basar.tracking.actor.archive.msg.MongoDBDocumentMessage;

/**
 * Provides an actor which receives a mongo db connection on initialization and writes
 * incoming (already to json converted) documents into a configured database collection.
 * The implementation assumes that the caller is not interested in any result thus it
 * simply consumes but does not respond to messages
 * @author mnxfst
 * @since 01.10.2013
 *
 * Revision Control Info $Id$
 */
class MongoDBDocumentWriter extends UntypedActor {

	private final MongoCollection mongoDBCollection;
	
	/**
	 * Initializes the document writer using the provided input
	 * @param mongoDBCollection
	 */
	public MongoDBDocumentWriter(final MongoCollection mongoDBCollection) {
		this.mongoDBCollection = mongoDBCollection;
	}
	


	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		// process message of given type only
		if(message instanceof MongoDBDocumentMessage) {
			
			MongoDBDocumentMessage msg = (MongoDBDocumentMessage)message;
			
			// create a document and add all contained key/value pairs
			DocumentBuilder document = BuilderFactory.start();
			for(String key : msg.getFields().keySet()) {
				Serializable value = msg.getFields().get(key);				
				document.add(key, value);
			}

			// write document to database aynchronously
			mongoDBCollection.insertAsync(document);			
		} else {
			unhandled(message);
		}
	}



}
