package org.iproduct.demo.profiler.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

public class ProfilerAppClient {
	public static final String PROFILER_API_URL = "http://localhost:8080/rest/api/";

	public static void main(String[] args) {
		// Default client instance
		Client client = ClientBuilder.newClient();
		
		WebTarget target = client.target(PROFILER_API_URL + "stats/sse");
		try (SseEventSource source = SseEventSource.target(target).build()) {
			source.register(System.out::println);
			source.open();
			Thread.sleep(5000); // Consume events for just 5000 ms
		} catch (InterruptedException e) {
			// falls through
		}
	}
}
