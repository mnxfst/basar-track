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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.util.List;
import java.util.Set;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.mnxfst.basar.tracking.cache.CacheRoot;
import com.mnxfst.basar.tracking.config.BasarTrackingServerConfiguration;
import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.db.DatabaseRoot;
import com.mnxfst.basar.tracking.gateway.ContractorMessageGateway;
import com.mnxfst.basar.tracking.gateway.message.DeregisterContractorMessage;
import com.mnxfst.basar.tracking.gateway.message.RegisterContractorMessage;
import com.mnxfst.basar.tracking.http.converter.HttpRequestConverter;
import com.mnxfst.basar.tracking.http.converter.message.HttpRequestMessage;
import com.mnxfst.basar.tracking.model.Metric;

/**
 * Core component required for ramping up the tracking server component<br/><br/> 
 * <b>Open Issues</b>
 * <ul>
 *   <li>Read metrics from configuration in a more generic way thus when new elements are added, code must not be changed</li> 
 * </ul>
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 *
 */
public class BasarTrackingServer {

	/** logging facility */
	private static final Logger logger = Logger.getLogger(BasarTrackingServer.class);

	/**
	 * Initializes the actor system and bootstraps the netty components
	 * @param configuration
	 * @throws Exception
	 */
	public void run(final BasarTrackingServerConfiguration configuration) throws Exception {
		
		ActorSystem actorSystem = setupActorSystem("btrack", configuration.getDatabaseServers(), configuration.getDatabaseName(),
				configuration.getDefaultTrackingEventCollection(), configuration.getContractors());
				
		// registering the page impression metric
		actorSystem.eventStream().publish(configuration.getMetrics().getPageImpression());
		
		RegisterContractorMessage regMsg = new RegisterContractorMessage("contractor2");
		regMsg.addMetric(new Metric("piMetric", "piMetric"));
		actorSystem.eventStream().publish(regMsg);
		
		// Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new BasarTrackingServerInitializer(actorSystem));

            Channel channel = b.bind(configuration.getPort()).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }        
	}

	/**
	 * Sets up and initializes the {@link ActorSystem actor system} used for handling inbound events
	 * @param actorSystemIdentifier
	 * @param databaseServers
	 * @param databaseName
	 * @param defaultTrackingEventCollection
	 * @param contractors
	 * @return
	 */
	protected ActorSystem setupActorSystem(final String actorSystemIdentifier, final List<String> databaseServers, final String databaseName, final String defaultTrackingEventCollection, final Set<String> contractors) {
		
		int numOfTrackingEventInstances = 1;
		MemcachedClient cacheClient = null;
		
		// initialize actor system by assigning the provided name
		final ActorSystem actorSystem = ActorSystem.create(actorSystemIdentifier);
		
		// initialize database actor which serves as root node for all actors accessing the database
		final ActorRef databaseRootRef = actorSystem.actorOf(Props.create(DatabaseRoot.class, databaseServers, numOfTrackingEventInstances), "dbRoot");
		// initialize cache actor which serves as root node for all actors accessing the cache layer
		final ActorRef cacheRootRef = actorSystem.actorOf(Props.create(CacheRoot.class, cacheClient), "cacheRoot");
		// initialize inbound message gateway which is responsible for fully setting up subsequent hierarchies
		final ActorRef gatewayRef = actorSystem.actorOf(Props.create(ContractorMessageGateway.class, databaseRootRef, cacheRootRef), "messageGateway");
		// initialize http request converter and assign it to the event stream
		final ActorRef httpRequestConverterRef = actorSystem.actorOf(Props.create(HttpRequestConverter.class, gatewayRef), "httpRequestConverter");
		
		// attach the request converter to the event stream as the tracking server simply publishes inbound requests on the internal bus
		actorSystem.eventStream().subscribe(httpRequestConverterRef, HttpRequestMessage.class);
		
		// attach the message gateway to the event stream to receive metric configuration via messages
		actorSystem.eventStream().subscribe(gatewayRef, BasarTrackingServerMetricConfigElement.class);
		actorSystem.eventStream().subscribe(gatewayRef, RegisterContractorMessage.class);
		actorSystem.eventStream().subscribe(gatewayRef, DeregisterContractorMessage.class);
		
		return actorSystem;
	}
	
	public static void main(String[] args) throws Exception {
		
		Options options = new Options();
		@SuppressWarnings("static-access")
		Option configFileOption = OptionBuilder.withArgName("file")
											.hasArg()
											.withDescription("configuration file name")
											.create("f");
		options.addOption(configFileOption);
		
		CommandLineParser clParser = new PosixParser();
		CommandLine cl = clParser.parse(options, args);

		// ensure that the option 'f' is contained in the command-line arguments, otherwise
		// print usage and return
		if(!cl.hasOption("f")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "ant", options );
			return;
		}
		
		String configurationFileName = cl.getOptionValue("f");
		logger.info("Reading server configuration from: " + configurationFileName);
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		new BasarTrackingServer().run(mapper.readValue(new File(configurationFileName), BasarTrackingServerConfiguration.class));
	}
	
}
