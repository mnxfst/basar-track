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

package com.mnxfst.basar.tracking.actor.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Holds all data extracted while decoding the query string of a request uri
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 */
public class DecodedQueryStringMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, List<String>> queryParameters;
	
	/**
	 * Initializes the message using the provided input
	 * @param queryParameters
	 */
	public DecodedQueryStringMessage(Map<String, List<String>> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public Map<String, List<String>> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(Map<String, List<String>> queryParameters) {
		this.queryParameters = queryParameters;
	}

}
