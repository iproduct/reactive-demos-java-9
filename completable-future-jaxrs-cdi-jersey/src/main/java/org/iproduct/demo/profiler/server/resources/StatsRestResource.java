/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.iproduct.demo.profiler.server.resources;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.OutboundSseEvent.Builder;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.iproduct.demo.profiler.cdi.qualifiers.CpuProfiling;
import org.iproduct.demo.profiler.server.model.CpuLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/stats")
@ApplicationScoped
public class StatsRestResource {
	private Logger logger = LoggerFactory.getLogger(StatsRestResource.class);

    private AtomicInteger i = new AtomicInteger();
    private SseBroadcaster broadcaster;
    private volatile Builder builder;
    
    @Context 
    public void setSse(Sse sse) {
        this.broadcaster = sse.newBroadcaster();
        this.builder = sse.newEventBuilder();
    }
        
    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void stats(@Context SseEventSink sink) {
        broadcaster.register(sink);
    }
    
    public void asyncProcessingCpuLoad(@ObservesAsync @CpuProfiling CpuLoad cpuLoad) {
    	OutboundSseEvent sseEvent = createStatsEvent(builder, cpuLoad);
    	broadcaster.broadcast(sseEvent);
    }

    private OutboundSseEvent createStatsEvent(final OutboundSseEvent.Builder builder, CpuLoad cpuLoad) {
    	OutboundSseEvent event = builder
    		.id(""+i.getAndIncrement())
        	.name("stats")
            .data(CpuLoad.class, cpuLoad)
            .mediaType(MediaType.APPLICATION_JSON_TYPE)
            .build();
    	return event;
    }
}
