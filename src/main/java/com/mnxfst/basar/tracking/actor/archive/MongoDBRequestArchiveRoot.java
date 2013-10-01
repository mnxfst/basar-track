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

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import io.netty.handler.codec.http.HttpRequest;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;
import akka.routing.RoundRobinRouter;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;

/**
 * Root entry into actor hierarchy which implements an archiving concept based
 * upon a mongo database instance/cluster. This node ensures the database connection,
 * restarts it after any exception and encapsulates the technical implementation
 * from the remaining parts of the application.<br/><br/>
 * The actor itself 
 * <ul>
 * <li>initiates the database connection</li>
 * <li>ramps up an sub-actor (behind a router configuration) which converts {@link HttpRequest requests} into their json representation</li>
 * <li>ramps up an sub-actor (behind a router configuration) which write json documents to database asynchronously</li> 
 * </ul> 
 * @author mnxfst
 * @since 01.10.2013
 *
 * Revision Control Info $Id$
 */
public class MongoDBRequestArchiveRoot extends UntypedActor {

	private final ActorRef mongoDBDocumentWriterRef;
	private final ActorRef mongoDBRequestDocumentConverterRef;
	private MongoClientConfiguration mongoDBClientConfiguration;
	private MongoClient mongoDBClient;
	
	public MongoDBRequestArchiveRoot() {
		
		this.mongoDBClientConfiguration = new MongoClientConfiguration();
		this.mongoDBClientConfiguration.addServer("localhost:27017");
        this.mongoDBClient = MongoFactory.createClient(mongoDBClientConfiguration);
        MongoDatabase database = mongoDBClient.getDatabase("bt");
        MongoCollection collection = database.getCollection("btreq");

        // TODO supervisor strategy
		// TODO each actor must have access via its own client?        
        
		this.mongoDBDocumentWriterRef = context().actorOf(Props.create(MongoDBDocumentWriter.class, collection).withRouter(new RoundRobinRouter(2)), "dbDocWriter");
		this.mongoDBRequestDocumentConverterRef = context().actorOf(Props.create(MongoDBRequestDocumentConverter.class, mongoDBDocumentWriterRef).withRouter(new RoundRobinRouter(5)), "dbDocConverter");
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof HttpRequest) {			
			this.mongoDBRequestDocumentConverterRef.tell(message, getSelf());			
		} 
	}

	/*
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(-1,
			      Duration.Inf(),
		          new Function<Throwable, Directive>() {
		            public Directive apply(Throwable cause) {
		            	System.out.println("error: " + cause);
		              return SupervisorStrategy.stop();
		            }
		          });
	}
	TODO implement strategy
*/
}
