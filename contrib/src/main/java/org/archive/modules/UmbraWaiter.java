/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.archive.modules;

import org.archive.crawler.event.AMQPUrlReceivedEvent;
import org.archive.crawler.event.StatSnapshotEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Bean to enforce a wait for Umbra amqp queue
 * 
 * @contributor 
 */
public class UmbraWaiter implements ApplicationListener<ApplicationEvent> {

    /**
     * We should setUmbraUrl true once we've received a URL from Umbra...
     */
    protected boolean umbraUrl = false;
    public boolean getUmbraUrl() {
        return umbraUrl;
    }
    public void setUmbraUrl(boolean umbraUrl) {
        this.umbraUrl = umbraUrl;
    }

    protected CrawlController controller;
    public CrawlController getCrawlController() {
        return this.controller;
    }
    @Autowired
    public void setCrawlController(CrawlController controller) {
        this.controller = controller;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AMQPUrlReceivedEvent) {
            setUmbraUrl(true);
        } else if (event instanceof StatSnapshotEvent) {
            checkUmbraWait();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof StatSnapshotEvent) {
            checkUmbraWait();
        }
    }

    protected void checkUmbraWait() {
        if (frontier.isEmpty() && getUmbraUrl()) {
            controller.requestCrawlStop(CrawlStatus.FINISHED);
        }
    }
}
