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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import akka.actor.ActorSystem;

/**
 * Initializes the request processing pipeline by adding decoder, encoder and handler
 * components as single stepts
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 */
public class BasarTrackingServerInitializer extends ChannelInitializer<SocketChannel> {

	private final ActorSystem actorSystem;
	
	/**
	 * Initializes the instance using the provided input
	 * @param actorSystem
	 */
	public BasarTrackingServerInitializer(final ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}
	
	/**
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	protected void initChannel(SocketChannel ch) throws Exception {
		
        ChannelPipeline p = ch.pipeline();
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());        
        p.addLast("handler", new BasarTrackingServerInboundHandler(actorSystem));	
	}

}
