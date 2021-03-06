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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

import org.iproduct.demo.profiler.server.model.CpuLoad;
import org.iproduct.demo.profiler.server.service.Profiler;

import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

@Path("/rxstats")
@ApplicationScoped
public class StatsRestResourceRxJava2 {
    private static final Random RANDOM = new Random();

    private SseBroadcaster broadcaster;
    private Builder builder;
    @Inject
    private Profiler profiler;


    @Context 
    public void setSse(Sse sse) {
        this.broadcaster = sse.newBroadcaster();
        this.builder = sse.newEventBuilder();
        
        Flowable
            .interval(500, TimeUnit.MILLISECONDS)
            .zipWith(
                Flowable.generate((Emitter<OutboundSseEvent.Builder> emitter) -> emitter.onNext(builder.name("stats"))),
                (id, bldr) -> createStatsEvent(bldr, id)
            )
            .subscribeOn(Schedulers.single())
            .subscribe(broadcaster::broadcast);
    }
    
    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void stats(@Context SseEventSink sink) {
        broadcaster.register(sink);
    }

    private OutboundSseEvent createStatsEvent(final OutboundSseEvent.Builder builder, final long eventId) {
        return builder
            .id("" + eventId)
            .data(CpuLoad.class, new CpuLoad(new Date().getTime(), (int) profiler.getJavaCPULoad(), profiler.areProcessesChanged()))
            .mediaType(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
    
}