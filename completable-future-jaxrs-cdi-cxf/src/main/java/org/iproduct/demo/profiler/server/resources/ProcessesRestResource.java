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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.OutboundSseEvent.Builder;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.iproduct.demo.profiler.cdi.qualifiers.CpuProfiling;
import org.iproduct.demo.profiler.server.model.ProcessInfo;
import org.iproduct.demo.profiler.server.service.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/processes")
@ApplicationScoped
public class ProcessesRestResource {
	private Logger logger = LoggerFactory.getLogger(ProcessesRestResource.class);

    @Inject
    Profiler profiler;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getJavaProcesses(@Suspended AsyncResponse asyncResp) {
    	CompletableFuture.supplyAsync(() -> {
    		List<ProcessInfo> result =  profiler.getJavaProcesses(); //List.of("A", "B", "C", "D"); //.stream().reduce("", (acc, item) -> acc + item);
    		System.out.println(result);
    		return result;
    	}).thenAccept(result -> asyncResp.resume(result));
    }
}
