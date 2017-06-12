package org.iproduct.demo.profiler.client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.sse.SseEventSource;

import org.iproduct.demo.profiler.client.model.ProcessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Run the ProfilerStreamingServer first (from completable-future-jaxrs-cdi-jersey project)

public class ProfilerAppClient {
	public static final String PROFILER_API_URL = "http://localhost:8080/rest/api/";
	public static Logger logger = LoggerFactory.getLogger(ProfilerAppClient.class);

	public static void main(String[] args) {
		// Default client instance
		Client client = ClientBuilder.newClient();
		final WebTarget processes = client.target(PROFILER_API_URL + "processes");
		final WebTarget stats = client.target(PROFILER_API_URL + "stats/sse");
		
		CompletionStage<List<ProcessInfo>> processesStage = processes.request()
			.rx()
			.get(new GenericType<List<ProcessInfo>>() {})
			.exceptionally(throwable -> {
				logger.error("Error receiving active Java processes: " + throwable.getMessage());
				return Collections.emptyList();
			});
		
		CompletionStage<Void> printProcessesStage =
			processesStage.thenApply(proc -> {
				System.out.println("Active JAVA Processes: " + proc); 
				return null;
			});
			
		printProcessesStage.thenRun( () -> {
			try (SseEventSource source = SseEventSource.target(stats).build()) {
				source.register(System.out::println);
				source.open();
				Thread.sleep(20000); // Consume events for 20 sec
			} catch (InterruptedException e) {
				logger.info("SSE consumer interrupted: " + e);
			}
		})
		.thenRun(() -> {System.exit(0);});
	}
}
