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

package com.mnxfst.basar.tracking.sandbox.dchh;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Simple actor being asked to say 'Hello'
 * @author mnxfst
 * @since 04.11.2013
 *
 * Revision Control Info $Id$
 */
public class Hello extends UntypedActor {

	private final ActorRef worldActorRef;
	
	public Hello() {
		this.worldActorRef = context().actorOf(Props.create(World.class), "world");
		System.out.println("wordActorRef: " + worldActorRef);
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof String) {
			worldActorRef.tell(message + " Hello", getSelf());
		}			
		
	}

}
