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

package com.mnxfst.basar.tracking.metric.pi;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jmockmongo.MockMongo;

import net.spy.memcached.MemcachedClient;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoClientConfiguration;
import com.allanbank.mongodb.MongoFactory;
import com.mnxfst.basar.tracking.cache.CacheRoot;
import com.mnxfst.basar.tracking.config.BasarTrackingServerMetricConfigElement;
import com.mnxfst.basar.tracking.db.DatabaseRoot;
import com.mnxfst.basar.tracking.metric.pi.cache.PageImpressionCacheWriter;
import com.mnxfst.basar.tracking.metric.pi.config.PageImpressionConfigElement;
import com.mnxfst.basar.tracking.metric.pi.db.PageImpressionDBWriter;
import com.mnxfst.basar.tracking.metric.pi.model.PageImpression;
import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

/**
 * Test case for {@link PageImpressionHandler}
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionHandlerTest {

	@Test
	public void testInit() throws Exception {
		
		 final MemCacheDaemon<LocalCacheElement> daemon = new MemCacheDaemon<LocalCacheElement>();

	        CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap.create(ConcurrentLinkedHashMap.EvictionPolicy.FIFO, 100, 10000000);
	        daemon.setCache(new CacheImpl(storage));
	        daemon.setBinary(false);
	        daemon.setAddr(new InetSocketAddress(1234));
	        daemon.setIdleTime(1000);
	        daemon.setVerbose(false);
	        daemon.start();
		
		MockMongo mock = new MockMongo();
		mock.start();

		MongoClientConfiguration config = new MongoClientConfiguration();
        config.addServer("localhost:27017");
        MongoClient mongoClient = MongoFactory.createClient(config);
		
        MemcachedClient cacheClient = new MemcachedClient(new InetSocketAddress(1234));
        
        ActorSystem system = ActorSystem.create("junit");
        
        List<String> servers = new ArrayList<>();
        servers.add("localhost:27017");
        final ActorRef dbRootRef = system.actorOf(Props.create(DatabaseRoot.class, servers), "dbRoot");
        final ActorRef cacheRootRef = system.actorOf(Props.create(CacheRoot.class, cacheClient), "cacheRoot");
        
        PageImpressionConfigElement cfg = new PageImpressionConfigElement();
        cfg.setCacheWriterClass(PageImpressionCacheWriter.class.getName());
        cfg.setCacheWriterId("cache");
        cfg.setDatabaseWriterClass(PageImpressionDBWriter.class.getName());
        cfg.setDatabaseWriterId("db");
        cfg.setIdentifier("pi");
        cfg.setMetricClass(PageImpression.class.getName());
        cfg.setNumCacheWriters(1);
        cfg.setNumDatabaseWriters(1);
        
        final ActorRef piRef = system.actorOf(Props.create(PageImpressionHandler.class, dbRootRef, cacheRootRef, cfg), "piRoot");
        Patterns.ask(arg0, arg1, new Timeout(100, TimeUnit.MILLISECONDS
        
        Thread.sleep(1000);
        system.shutdown();
		mock.stop();
		daemon.stop();
	}
	
}
