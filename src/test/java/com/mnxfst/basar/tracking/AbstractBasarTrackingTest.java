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

package com.mnxfst.basar.tracking;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;

/**
 * Common base class for test cases within the basar tracking context. A
 * set of convenience methods is provided by this implementation. 
 * @author mnxfst
 * @since 05.11.2013
 *
 * Revision Control Info $Id$
 */
public abstract class AbstractBasarTrackingTest {

	protected static ActorSystem system;
	
	/**
	 * Ramps up an actor system
	 */
	@BeforeClass
	public static void setup() {
		system = ActorSystem.create("databaseRootTest");
	}
	
	/**
	 * Properly shuts down the actor system
	 */
	@AfterClass
	public static void shutdown() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}

	
}
