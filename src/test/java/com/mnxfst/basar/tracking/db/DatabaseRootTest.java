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

package com.mnxfst.basar.tracking.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;

import com.mnxfst.basar.tracking.AbstractBasarTrackingTest;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage;
import com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterSuccessMessage;
import com.mnxfst.basar.tracking.metric.pi.PageImpressionDBWriter;

/**
 * Test cases for {@link DatabaseRoot}
 * @author mnxfst
 * @since 04.11.2013
 *
 * Revision Control Info $Id$
 */
public class DatabaseRootTest extends AbstractBasarTrackingTest {

	/**
	 * Test case for {@link DatabaseRoot#DatabaseRoot(java.util.List)} being provided null as input
	 */
	@Test
	public void testConstructorWithNullInput() {
		try {
			new DatabaseRoot(null, 1);
			Assert.fail("Invalid input provided to constructor");
		} catch(RuntimeException e) {
			// 
		}
	}

	/**
	 * Test case for {@link DatabaseRoot#DatabaseRoot(java.util.List)} being provided an empty list as input
	 */
	@Test
	public void testConstructorWithEmptyInput() {
		try {
			new DatabaseRoot(new ArrayList<String>(), 1);
			Assert.fail("Invalid input provided to constructor");
		} catch(RuntimeException e) {
			// 
		}
	}
	
	// TODO showcase
//	/**
//	 * Test case for {@link DatabaseRoot#handleDatabaseRegistration(com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage)} being provided
//	 * null as input
//	 */
//	@Test
//	public void testHandleDatabaseRegistrationWithNullInput() {
//		List<String> databaseServers = new ArrayList<>();
//		databaseServers.add("localhost:27017");
//		DatabaseRoot dbRoot = new DatabaseRoot(databaseServers);
//		dbRoot.handleDatabaseRegistration(null); // no (side) effects at all
//	}
	
	
	/**
	 * Test case for {@link DatabaseRoot#handleDatabaseRegistration(com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage)} being provided
	 * null as input
	 */
	@Test
	public void testHandleDatabaseRegistrationWithNullInput() {

		List<String> databaseServers = new ArrayList<>();
		databaseServers.add("localhost:27017");

		TestActorRef<DatabaseRoot> dbRootActor = TestActorRef.create(system, Props.create(DatabaseRoot.class, databaseServers));

		dbRootActor.underlyingActor().handleDatabaseRegistration(null); // no (side) effects at all
	}
	
	/**
	 * Test case for {@link DatabaseRoot#handleDatabaseRegistration(com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage)} being provided
	 * a valid registration message. This is a pure unit test....
	 */
	@Test
	public void testHandleDatabaseRegistrationWithValidRegistrationMessage() {

		// database root node configuration
		List<String> databaseServers = new ArrayList<>();
		databaseServers.add("localhost:27017");

		// create an actor instance using the testing framework
		TestActorRef<DatabaseRoot> dbRootRef = TestActorRef.create(system, Props.create(DatabaseRoot.class, databaseServers));

		// execute method under test
		RegisterDatabaseWriterSuccessMessage successMessage = 
				(RegisterDatabaseWriterSuccessMessage)dbRootRef.underlyingActor().handleDatabaseRegistration(
						new RegisterDatabaseWriterMessage("testComponent1", PageImpressionDBWriter.class.getName(), 1));
		
		Assert.assertNotNull("The response must not be null", successMessage);
		Assert.assertEquals("The component identifier must be testComponent1", "testComponent1", successMessage.getComponentId());

		// ensures that the underlying actor instance has one writer which is the same as the one received in the message response 
		Assert.assertEquals("The map of registered database writers must contain 1 element", 1, dbRootRef.underlyingActor().getRegisteredDatabaseWriters().size());
		ActorRef dbWriterRef = dbRootRef.underlyingActor().getRegisteredDatabaseWriters().get("testComponent1");
		Assert.assertNotNull("The reference must not be null", dbWriterRef);
		Assert.assertEquals("The actor references must be equal", dbWriterRef, successMessage.getComponentRef());
	}
	
	/**
	 * Test case for {@link DatabaseRoot#handleDatabaseRegistration(com.mnxfst.basar.tracking.db.message.RegisterDatabaseWriterMessage)} being provided
	 * a valid registration message. The test uses the JavaTestKit to show async testing
	 */
	@Test
	public void testHandleDatabaseRegistrationWithValidRegistrationMessageAsync() {

		// database root node configuration
		final List<String> databaseServers = new ArrayList<>();
		databaseServers.add("localhost:27017");

		new JavaTestKit(system)  {{
			
			// create database root node which is tested further down below
			final Props props = Props.create(DatabaseRoot.class, databaseServers);
			final TestActorRef<DatabaseRoot> dbRootRef = TestActorRef.create(system, props);
			
			// ensure that the underlying actor instance currently has no registered database writers 
			Assert.assertTrue("The map of registered database writers must be empty", dbRootRef.underlyingActor().getRegisteredDatabaseWriters().isEmpty());
						
			// tell the root to register a new database writer 
			dbRootRef.tell(new RegisterDatabaseWriterMessage("testComponent1", PageImpressionDBWriter.class.getName(), 1), getRef());
			
			// retrieve response ... which must contain exactly one element ... and ensure it has the expected types and values
			Object[] receivedMessage = receiveN(1, duration("1 second"));
			Assert.assertEquals("The message array must hold 1 element", 1, receivedMessage.length);
			Assert.assertTrue("The message must be of type '"+RegisterDatabaseWriterSuccessMessage.class.getName()+"'", receivedMessage[0] instanceof RegisterDatabaseWriterSuccessMessage);
			RegisterDatabaseWriterSuccessMessage successMessage = (RegisterDatabaseWriterSuccessMessage)receivedMessage[0];
			Assert.assertEquals("The component identifier must be testComponent1", "testComponent1", successMessage.getComponentId());
			Assert.assertNotNull("The component reference must not be null", successMessage.getComponentRef());					
						
			// ensures that the underlying actor instance has one writer which is the same as the one received in the message response 
			Assert.assertEquals("The map of registered database writers must contain 1 element", 1, dbRootRef.underlyingActor().getRegisteredDatabaseWriters().size());
			ActorRef dbWriterRef = dbRootRef.underlyingActor().getRegisteredDatabaseWriters().get("testComponent1");
			Assert.assertNotNull("The reference must not be null", dbWriterRef);
			Assert.assertEquals("The actor references must be equal", dbWriterRef, successMessage.getComponentRef());

		}};		
	}
	
}
