package org.iproduct.demo.profiler.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

//import javax.ws.rs.core.GenericType;
//import javax.ws.rs.sse.SseEventSource;

import org.iproduct.demo.profiler.client.model.ProcessInfo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;


public class ProfilerAppClient {
	public static final String PROFILER_API_URL = "http://localhost:8080/rest/api/";


	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest processesReq = HttpRequest.newBuilder()
			    .uri(new URI(PROFILER_API_URL + "processes"))
			    .GET()
			    .build();

		HttpRequest statsReq = HttpRequest.newBuilder()
			    .uri(new URI(PROFILER_API_URL + "stats/sse"))
			    .GET()
			    .build();

		TypeToken<ArrayList<ProcessInfo>> token = new TypeToken<ArrayList<ProcessInfo>>() {};
		Gson gson = new GsonBuilder().create();
		
		client.sendAsync(processesReq, HttpResponse.BodyHandler.asString())
			.thenApply( (HttpResponse<String> processesStr) -> {
			    List<ProcessInfo> something = gson.fromJson(processesStr.body(), token.getType());
				return something;
			}).thenApply(proc -> {
				proc.stream().forEach(System.out::println);
				return null;
			}).exceptionally((Throwable ex) -> { 
				System.out.println("Error: " + ex); 
				return null;
			}).thenRun(() -> {System.exit(0);});

		
		Thread.sleep(5000);
		
	}
}
