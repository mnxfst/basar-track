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
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import akka.actor.ActorSystem;

/**
 * Core component required for ramping up the tracking server component
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 */
public class BasarTrackingServer {

	public void run(final int port) throws Exception {
		
		ActorSystem actorSystem = ActorSystem.create("basar-track");
//		final ActorRef actorRef = actorSystem.actorOf(Props.create(DecodedQueryStringProcessingActor.class).withRouter(new RoundRobinRouter(1)), "queryDecoder");		
//		final ActorRef mongoDBRequestArchiverRef = actorSystem.actorOf(Props.create(MongoDBRequestArchiveRoot.class).withRouter(new RoundRobinRouter(1)), "archiver");
//		actorSystem.eventStream().subscribe(actorRef, HttpRequest.class);
//		actorSystem.eventStream().subscribe(mongoDBRequestArchiverRef, HttpRequest.class);
		
		
		// Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
//            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new BasarTrackingServerInitializer(actorSystem));

            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        
	}
	
	public static void main(String[] args) throws Exception {
		new BasarTrackingServer().run(8080);
	}
	
}
