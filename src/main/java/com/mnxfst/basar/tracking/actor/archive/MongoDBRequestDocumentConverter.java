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

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Iterator;
import java.util.List;

import scala.concurrent.duration.Duration;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;

import com.mnxfst.basar.tracking.actor.archive.msg.MongoDBDocumentMessage;

/**
 * Converts inbound {@link HttpRequest requests} into {@link MongoDBDocumentMessage document messages}
 * which are later on inserted into the database by {@link MongoDBDocumentWriter}.
 * @author mnxfst
 * @since 01.10.2013
 *
 * Revision Control Info $Id$
 */
public class MongoDBRequestDocumentConverter extends UntypedActor {

	// reference towards writer which inserts the converted request document into database
	private final ActorRef mongoDBDocumentWriterRef;
	
	/**
	 * Initializes the instance using the provided input
	 * @param mongoDBDocumentWriterRef
	 */
	public MongoDBRequestDocumentConverter(final ActorRef mongoDBDocumentWriterRef) {
		this.mongoDBDocumentWriterRef = mongoDBDocumentWriterRef;
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof HttpRequest) {
			
			MongoDBDocumentMessage documentMessage = new MongoDBDocumentMessage();
			
			HttpRequest request = (HttpRequest)message;
			
			// transfer header fields
			for(String headerName : request.headers().names()) {
				String headerValue = request.headers().get(headerName); // TODO getAll??
				documentMessage.addField(headerName, headerValue);
			}
			
			// transfer query string
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
			for(String reqParam : queryStringDecoder.parameters().keySet()) {
				List<String> reqParamValues = queryStringDecoder.parameters().get(reqParam);
				StringBuffer combinedValues = new StringBuffer();
				for(Iterator<String> rpvIter = reqParamValues.iterator(); rpvIter.hasNext();) {
					combinedValues.append(rpvIter.next());
					if(rpvIter.hasNext())
						combinedValues.append(", ");
				}
				documentMessage.addField(reqParam, combinedValues.toString());
			}
			
			documentMessage.addField("method", request.getMethod().toString());
			documentMessage.addField("protocol-version", request.getProtocolVersion().text());
			documentMessage.addField("uri", request.getUri());
			
			mongoDBDocumentWriterRef.tell(documentMessage, getSender());
		} else {
			unhandled(message);
		}
	}

}
