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
package org.iproduct.demo.profiler.server.app;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.sse.SseFeature;
import org.iproduct.demo.profiler.server.resources.ProcessesRestResource;
import org.iproduct.demo.profiler.server.resources.StatsRestResource;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@ApplicationPath("api")
@ApplicationScoped
public class ProfilerApplication extends Application {
    @Inject private StatsRestResource statsRestService;
//	   @Inject private StatsRestServiceRxJava2 statsRestService;
	@Inject private ProcessesRestResource processesRestService;

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<>();
        singletons.add(new SseFeature());
        singletons.add(statsRestService);
        singletons.add(processesRestService);
        singletons.add(new JacksonJsonProvider());
        return singletons;
    }
}
