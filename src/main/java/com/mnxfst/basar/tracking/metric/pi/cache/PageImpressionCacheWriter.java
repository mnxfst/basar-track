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

package com.mnxfst.basar.tracking.metric.pi.cache;

import org.apache.commons.lang.StringUtils;

import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

import com.mnxfst.basar.tracking.cache.CacheValueWriter;
import com.mnxfst.basar.tracking.metric.pi.PageImpressionHandler;
import com.mnxfst.basar.tracking.metric.pi.message.PageImpressionWriteMessage;
import com.mnxfst.basar.tracking.metric.pi.model.PageImpression;

/**
 * Writes/updates cached page impression values. The writer expects the same message types as {@link PageImpressionHandler}. 
 * @author mnxfst
 * @since 29.10.2013
 *
 * Revision Control Info $Id$
 */
public class PageImpressionCacheWriter extends CacheValueWriter {

	/**
	 * Initializes the cache writer using the provided input
	 * @param memcachedClient
	 */
	public PageImpressionCacheWriter(final MemcachedClient memcachedClient) {
		super(memcachedClient);
	}
	
	/**
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	public void onReceive(Object message) throws Exception {

		if(message instanceof PageImpressionWriteMessage) {
			updateCachedPageImpression((PageImpressionWriteMessage)message, false); // TODO set to true to enable locks
		} else {
			unhandled(message);
		}
	}
	
	/**
	 * Updates the referenced cache value by reading its current content, adding the
	 * provided {@link PageImpressionWriteMessage#getCount() count} and writing the 
	 * element back to cache. The update can be performed using CAS.
	 * @param message
	 * @param useCAS use CASE (concurrency control)
	 */
	protected void updateCachedPageImpression(final PageImpressionWriteMessage message, final boolean useCAS) {
		
		// allow updates only if the required information are available
		if(message != null && StringUtils.isNotBlank(message.getContractorIdentifier()) && StringUtils.isNotBlank(message.getPageImpressionSource()) && message.getCount() > 0) {
			
			// calculate the cache key for the inbound message
			String cacheKey = calculateCacheKey(message);
			
			if(useCAS) {
				CASValue<Object> value = readCacheValueAndLock(cacheKey);
				PageImpression pi = (PageImpression)value.getValue();
				if(pi != null)
					pi.addCount(message.getCount());
				else 
					pi = new PageImpression(message.getContractorIdentifier(), message.getPageImpressionSource(), message.getCount());			
				CASValue<Object> updatedPi = new CASValue<Object>(value.getCas(), pi);
				writeCacheValue(cacheKey, updatedPi);
			} else {
				PageImpression pi = (PageImpression)readCacheValue(cacheKey);
				if(pi != null)
					pi.addCount(message.getCount());
				else 
					pi = new PageImpression(message.getContractorIdentifier(), message.getPageImpressionSource(), message.getCount());			
				writeCacheValue(cacheKey, pi);
			}
		}
	}
	
	/**
	 * Calculates the unique cache key for the provided message
	 * @param message
	 * @return
	 */
	protected String calculateCacheKey(final PageImpressionWriteMessage message) {
		StringBuffer buf = new StringBuffer();
		buf.append(message.getContractorIdentifier());
		buf.append("-pi-");
		buf.append(message.getPageImpressionSource());
		return buf.toString();
	}

}
