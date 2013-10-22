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

package com.mnxfst.basar.tracking.classical;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Iterator;
import java.util.List;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;

/**
 * Classical / sequential implementation if tracking data handler. This must not be
 * used in production mode unless you know what you are doing as it is sample code
 * for comparing modern vs classical programming styles
 * @author mnxfst
 * @since 08.10.2013
 *
 * Revision Control Info $Id$
 */
public class SequentialTrackingDataHandler {

	private MongoClientConfiguration mongoDBClientConfiguration;
	private MongoClient mongoDBClient;
	private MongoDatabase database;
	private MongoCollection collection;

	public SequentialTrackingDataHandler() {
		this.mongoDBClientConfiguration = new MongoClientConfiguration();
		this.mongoDBClientConfiguration.addServer("localhost:27017");
        this.mongoDBClient = MongoFactory.createClient(mongoDBClientConfiguration);
        this.database = mongoDBClient.getDatabase("bt");
        this.collection = database.getCollection("btreq");
	}
	
	public void persistHttpRequest(final HttpRequest request) {
		
		// create a document and add all contained key/value pairs
		DocumentBuilder document = BuilderFactory.start();

		for(String headerName : request.headers().names()) {
			String headerValue = request.headers().get(headerName); // TODO getAll??
			document.add(headerName, headerValue);
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
			document.add(reqParam, combinedValues.toString());
		}
					
		document.add("method", request.getMethod().toString());
		document.add("protocol-version", request.getProtocolVersion().text());
		document.add("uri", request.getUri());

		// write document to database aynchronously
		collection.insertAsync(document);	
		
	}
	
	
	
}
