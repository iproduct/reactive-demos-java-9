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
package org.iproduct.demo.profiler.server;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.iproduct.demo.profiler.server.app.ProfilerApplication;
import org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener;
import org.jboss.weld.environment.servlet.Listener;

//--add-modules=java.se.ee --permit-illegal-access  --patch-module java.xml.ws.annotation=./myannotation.jar

public final class ProfilerStreamingServer {
	private static final URI BASE_URI = UriBuilder.fromUri("http://localhost:8080/rest").port(8080).build();

	public static void main(final String[] args) throws Exception {
		
		final Server server = new Server(8080);
		
		final ServletContainer jerseyServlet = new ServletContainer();
		final ServletHolder jerseyServletHolder = new ServletHolder(jerseyServlet);
		jerseyServletHolder.setInitParameter("javax.ws.rs.Application", ProfilerApplication.class.getName());
		
		final ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
	
		// Add CDI (Weld) listener
		context.addEventListener(new Listener());
		context.addEventListener(new BeanManagerResourceBindingListener());
		     
		context.addServlet(jerseyServletHolder, "/rest/api/*");
		
			

		final ServletHolder staticHolder = new ServletHolder(new DefaultServlet());
		final ServletContextHandler staticContext = new ServletContextHandler();
		staticContext.setContextPath("/javacpu");
		staticContext.addServlet(staticHolder, "/*");
		staticContext.setResourceBase(ProfilerStreamingServer.class.getResource("/web-ui").toURI().toString());

		HandlerList handlers = new HandlerList();
		handlers.addHandler(staticContext);
		handlers.addHandler(context);
		server.setHandler(handlers);

		server.start();
		server.join(); // Jetty

		// server.shutdownNow(); //Grizzly
//		weld.shutdown();
	}

	public static ResourceConfig createJaxRsApp() {
		return ResourceConfig.forApplicationClass(ProfilerApplication.class);
	}
}
