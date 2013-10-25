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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import org.apache.log4j.Logger;

import akka.actor.ActorSystem;

import com.mnxfst.basar.tracking.http.message.HttpRequestMessage;

/**
 * Core {@link HttpRequest request} handler which receives all inbound traffic, forwards it into the asynchronous
 * processing pipeline and responds with an empty tracking pixel.
 * @author mnxfst
 * @since 27.09.2013
 *
 * Revision Control Info $Id$
 */
public class BasarTrackingServerInboundHandler extends SimpleChannelInboundHandler<Object>  {

	/** logging facility */
	private static final Logger logger = Logger.getLogger(BasarTrackingServerInboundHandler.class);
	
	/** returned 1x1 pixel */
	private static byte[] trackingPng = {(byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,0x08,0x06,0x00,0x00,0x00,0x1F,0x15,(byte)0xC4,(byte)0x89,0x00,0x00,0x00,0x0B,0x49,0x44,0x41,0x54,0x78,(byte)0xDA,0x63,0x60,0x00,0x02,0x00,0x00,0x05,0x00,0x01,(byte)0xE9,(byte)0xFA,(byte)0xDC,(byte)0xD8,0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44,(byte)0xAE,0x42,0x60,(byte)0x82};
	/** size of pixel required for setting response header properly */
	private static int trackingPngLenght = trackingPng.length;
	
	/** entry point towards asynchronous request processing pipeline */
	private final ActorSystem actorSystem;
	
	/**
	 * Initializes the http request handler using the provided input
	 * @param actorSystem
	 */
	public BasarTrackingServerInboundHandler(final ActorSystem actorSystem) {
		this.actorSystem = actorSystem;		
	}
	
	/**
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof HttpRequest) {

			// forward request into actor hierarchy for asynchronous processing and freeing up resources for serving upcoming requests
			// no special actor is targeted but the request is published on the event stream accessible for all "root" level actors
			HttpRequestMessage requestMessage = new HttpRequestMessage();
			requestMessage.setInboundInterface("http");
			requestMessage.setRequest((HttpRequest)msg);
			requestMessage.setTimestamp(System.currentTimeMillis());
			actorSystem.eventStream().publish(requestMessage);

			logger.info(this);
			
			// respond with 1x1 empty pixel
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(trackingPng));
			response.headers().set("Content-Type", "image/gif");
			response.headers().set("Content-Length", trackingPngLenght);
			ctx.write(response);
//			ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
