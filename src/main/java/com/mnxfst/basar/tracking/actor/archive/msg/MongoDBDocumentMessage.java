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

package com.mnxfst.basar.tracking.actor.archive.msg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a document to be inserted into a mongo db instance
 * @author mnxfst
 * @since 01.10.2013
 *
 * Revision Control Info $Id$
 */
public class MongoDBDocumentMessage implements Serializable {

	private static final long serialVersionUID = -4272593401494657801L;

	private Map<String, Serializable> fields = new HashMap<>();
	
	/**
	 * Default constructor - obviously ;-)
	 */
	public MongoDBDocumentMessage() {		
	}
	
	/**
	 * Adds a key/value pair to the documents fields
	 * @param key
	 * @param value
	 */
	public void addField(String key, Serializable value) {
		this.fields.put(key, value);
	}

	public Map<String, Serializable> getFields() {
		return fields;
	}

	public void setFields(Map<String, Serializable> fields) {
		this.fields = fields;
	}
	
	
}
