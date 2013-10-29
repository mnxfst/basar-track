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

package com.mnxfst.basar.tracking.config;

import java.io.Serializable;

import com.mnxfst.basar.tracking.metric.pi.PageImpressionConfigElement;

/**
 * Root element to metrics configuration
 * @author mnxfst
 * @since 30.10.2013
 *
 * Revision Control Info $Id$
 */
public class BasarTrackingServerMetricsConfigElement implements Serializable {

	private static final long serialVersionUID = -7497321017351782699L;

	private PageImpressionConfigElement pageImpression = new PageImpressionConfigElement();
	
	/**
	 * Default constructor
	 */
	public BasarTrackingServerMetricsConfigElement() {		
	}

	public PageImpressionConfigElement getPageImpression() {
		return pageImpression;
	}

	public void setPageImpression(PageImpressionConfigElement pageImpression) {
		this.pageImpression = pageImpression;
	}
	
	
	
}
