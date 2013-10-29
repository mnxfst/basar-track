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

package com.mnxfst.basar.tracking.store;

import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;

/**
 * Common parent to all nodes that wish to write data to the in-memory cache. This implementation provides
 * an expected constructor interface that all extending classes must comply with. Although other nodes are
 * allowed to access the cache as well children of a common cache root share the same {@link SupervisorStrategy supervisor strategy}
 * they benefit from. 
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public abstract class CacheValueWriter extends UntypedActor {

	/** default expiration time */
	private final static int expirationTime = 60*60*24*29;

	private final MemcachedClient memcachedClient;
	
	/**
	 * Initializes the cache value writer using the provided input
	 * @param memcachedClient
	 */
	public CacheValueWriter(final MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	
	/**
	 * Returns and locks the value associated with the given key
	 * @param key
	 * @return
	 */
	public CASValue<Object> readCacheValueAndLock(final String key) {
		return this.memcachedClient.gets(key);
	}
	
	/**
	 * Write and unlock value
	 * @param key
	 * @param value
	 */
	public void writeCacheValue(final String key, final CASValue<Object> value) {
		this.memcachedClient.asyncCAS(key, value.getCas(), value.getValue());
	}
	
	/**
	 * Returns the value associated with the provided key
	 * @param key
	 * @return
	 */
	public Object readCacheValue(final String key) {
		return this.memcachedClient.get(key);
	}
	
	/**
	 * Writes the provided value to cache
	 * @param key
	 * @param object
	 */
	public void writeCacheValue(final String key, final Object object) {
		this.memcachedClient.set(key, expirationTime, object); 
	}
	
}
