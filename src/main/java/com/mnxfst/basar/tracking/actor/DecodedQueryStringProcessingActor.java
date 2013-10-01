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

package com.mnxfst.basar.tracking.actor;

import io.netty.handler.codec.http.HttpRequest;

import java.util.List;

import akka.actor.UntypedActor;

import com.mnxfst.basar.tracking.actor.message.DecodedQueryStringMessage;

/**
 * Receives all inbound HTTP requests
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 */
public class DecodedQueryStringProcessingActor extends UntypedActor {

	private int count = 0;
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		count = count + 1;

		if(message instanceof HttpRequest) {

//			System.out.println("dec: " + message);
//			System.out.println(msg);
//			for(String paramKey : msg.getQueryParameters().keySet()) {
//				List<String> vals = msg.getQueryParameters().get(paramKey);
//				for(String v : vals) {
//					//
//				}
//			}
		}

		
	}

}
