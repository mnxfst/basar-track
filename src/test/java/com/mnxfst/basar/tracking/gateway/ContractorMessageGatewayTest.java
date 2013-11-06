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

package com.mnxfst.basar.tracking.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.InvalidActorNameException;
import akka.actor.Props;
import akka.testkit.TestActorRef;

import com.mnxfst.basar.tracking.AbstractBasarTrackingTest;
import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage;
import com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage;
import com.mnxfst.basar.tracking.metric.pi.PageImpressionConfigElement;
import com.mnxfst.basar.tracking.metric.pi.PageImpressionHandler;
import com.mnxfst.basar.tracking.model.Metric;

/**
 * Test case for {@link ContractorMessageGateway}
 * @author mnxfst
 * @since 05.11.2013
 *
 * Revision Control Info $Id$
 */
public class ContractorMessageGatewayTest extends AbstractBasarTrackingTest {

	/**
	 * Test case for {@link ContractorMessageGateway#instantiateMetricHandler(com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement)} being provided
	 * an unknown class as input
	 */
	@Test
	public void testInstantiateMetricHandlerWithUnknownClass() throws Exception { 		
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass("unknown class");
		try {
			gatewayActor.underlyingActor().instantiateMetricHandler(cfg);
			Assert.fail("Unknown class provided");
		} catch(ClassNotFoundException e) {
			//
		}
	}

	/**
	 * Test case for {@link ContractorMessageGateway#instantiateMetricHandler(com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement)} being provided
	 * a valid class but null as name for the actor to be created
	 */
	@Test
	public void testInstantiateMetricHandlerWithInvalidHandlerId() throws Exception { 		
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier(null);
		try {
			gatewayActor.underlyingActor().instantiateMetricHandler(cfg);
			Assert.fail("Empty actor names are not allowed");
		} catch(InvalidActorNameException e) {
			//
		}
	}

	/**
	 * Test case for {@link ContractorMessageGateway#instantiateMetricHandler(com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement)} being provided
	 * a valid configuration
	 */
	@Test
	public void testInstantiateMetricHandlerWithValidConfig() throws Exception { 		
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");		
		final ActorRef handlerRef = gatewayActor.underlyingActor().instantiateMetricHandler(cfg);
		Assert.assertNotNull("The handler must not be null", handlerRef);
		Assert.assertEquals("The name must be test-id", "test-id", handlerRef.path().name());
		Assert.assertTrue("The map of registered handlers must still be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * null as input
	 */
	@Test
	public void testInitMetricHandlersWithNullInput() {
		
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		gatewayActor.underlyingActor().initMetricHandlers(null);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());

	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * an empty list
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testInitMetricHandlersWithEmptyList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		gatewayActor.underlyingActor().initMetricHandlers(Collections.EMPTY_LIST);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * a list with null elements
	 */
	@Test
	public void testInitMetricHandlersWithNullElementsList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(null);
		cfgList.add(null);
		cfgList.add(null);
		cfgList.add(null);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * a list with invalid cfg element (empty identifier)
	 */
	@Test
	public void testInitMetricHandlersWithEmptyIdentifierInCfgList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier(null);
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * a list with invalid cfg element (empty class)
	 */
	@Test
	public void testInitMetricHandlersWithEmptyClassInCfgList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(null);
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * a list with invalid cfg element (invalid class)
	 */
	@Test
	public void testInitMetricHandlersWithInvalidClassInCfgList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass("unknown class");
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#initMetricHandlers(java.util.List)} being provided
	 * a list with valid cfg element
	 */
	@Test
	public void testInitMetricHandlersWithValidClassInCfgList() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of registered handlers must be empty", gatewayActor.underlyingActor().getMetricHandlers().isEmpty());
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertEquals("The number of registered handlers must be 1", 1, gatewayActor.underlyingActor().getMetricHandlers().size());
		Assert.assertTrue("The registered handler must be associated with test-id", gatewayActor.underlyingActor().getMetricHandlers().containsKey("test-id"));
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#registerContractor(com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage)}
	 * being provided null as input
	 */
	@Test
	public void testRegisterContractorWithNullInput() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());		
		gatewayActor.underlyingActor().registerContractor(null);
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#registerContractor(com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage)}
	 * being provided null as contractor id
	 */
	@Test
	public void testRegisterContractorWithNullContractorId() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());		
		gatewayActor.underlyingActor().registerContractor(new RegisterContractorMessage(null));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#registerContractor(com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage)}
	 * being provided empty metrics and kpis
	 */
	@Test
	public void testRegisterContractorWithEmptyMetricsAndKPIs() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());		
		gatewayActor.underlyingActor().registerContractor(new RegisterContractorMessage("test"));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#registerContractor(com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage)}
	 * being provided unknown metrics 
	 */
	@Test
	public void testRegisterContractorWithUnknownMetrics() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
		RegisterContractorMessage msg = new RegisterContractorMessage("test");
		msg.addMetric(new Metric("unknown", "description"));
		gatewayActor.underlyingActor().registerContractor(msg);
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#registerContractor(com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage)}
	 * being provided known metric 
	 */
	@Test
	public void testRegisterContractorWithKnownMetric() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertEquals("The number of registered handlers must be 1", 1, gatewayActor.underlyingActor().getMetricHandlers().size());
		Assert.assertTrue("The registered handler must be associated with test-id", gatewayActor.underlyingActor().getMetricHandlers().containsKey("test-id"));

		
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
		RegisterContractorMessage msg = new RegisterContractorMessage("test");
		msg.addMetric(new Metric("test-id", "description"));
		gatewayActor.underlyingActor().registerContractor(msg);
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#deregisterContractor(com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage)} 
	 * being provided null as input
	 */
	@Test
	public void testDeregisterContractorWithNullInput() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertEquals("The number of registered handlers must be 1", 1, gatewayActor.underlyingActor().getMetricHandlers().size());
		Assert.assertTrue("The registered handler must be associated with test-id", gatewayActor.underlyingActor().getMetricHandlers().containsKey("test-id"));

		
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
		RegisterContractorMessage msg = new RegisterContractorMessage("test");
		msg.addMetric(new Metric("test-id", "description"));
		gatewayActor.underlyingActor().registerContractor(msg);
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
		
		gatewayActor.underlyingActor().deregisterContractor(null);
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
	}
	
	/**
	 * Test case for {@link ContractorMessageGateway#deregisterContractor(com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage)} 
	 * being provided an unknown contractor id
	 */
	@Test
	public void testDeregisterContractorWithUnknownContractorId() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertEquals("The number of registered handlers must be 1", 1, gatewayActor.underlyingActor().getMetricHandlers().size());
		Assert.assertTrue("The registered handler must be associated with test-id", gatewayActor.underlyingActor().getMetricHandlers().containsKey("test-id"));

		
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
		RegisterContractorMessage msg = new RegisterContractorMessage("test");
		msg.addMetric(new Metric("test-id", "description"));
		gatewayActor.underlyingActor().registerContractor(msg);
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
		
		gatewayActor.underlyingActor().deregisterContractor(new DeregisterContractorMessage("unknown"));
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
	}

	/**
	 * Test case for {@link ContractorMessageGateway#deregisterContractor(com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage)} 
	 * being provided a known / registered contractor 
	 */
	@Test
	public void testDeregisterContractorWithKnownContractor() {
		TestActorRef<ContractorMessageGateway> gatewayActor = TestActorRef.create(system, Props.create(ContractorMessageGateway.class, null, null));
		
		PageImpressionConfigElement cfg = new PageImpressionConfigElement();
		cfg.setMetricClass(PageImpressionHandler.class.getName());
		cfg.setIdentifier("test-id");
		List<BasarTrackingServerMetricConfigElement> cfgList = new ArrayList<>();
		cfgList.add(cfg);
		gatewayActor.underlyingActor().initMetricHandlers(cfgList);
		Assert.assertEquals("The number of registered handlers must be 1", 1, gatewayActor.underlyingActor().getMetricHandlers().size());
		Assert.assertTrue("The registered handler must be associated with test-id", gatewayActor.underlyingActor().getMetricHandlers().containsKey("test-id"));

		
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
		RegisterContractorMessage msg = new RegisterContractorMessage("test");
		msg.addMetric(new Metric("test-id", "description"));
		gatewayActor.underlyingActor().registerContractor(msg);
		Assert.assertEquals("The map of contractors registered for metrics must contain 1 element", 1, gatewayActor.underlyingActor().getContractorMetrics().size());
		
		gatewayActor.underlyingActor().deregisterContractor(new DeregisterContractorMessage("test"));
		Assert.assertTrue("The map of contractors registered for metrics must be empty", gatewayActor.underlyingActor().getContractorMetrics().isEmpty());
	}
}
