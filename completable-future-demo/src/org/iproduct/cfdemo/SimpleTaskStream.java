package org.iproduct.cfdemo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.rmi.CORBA.Util;

public class SimpleTaskStream {

	public void testTaskSequence() {
		List<String> results = IntStream.range(0, 20)
			.boxed()
			.map(this::longTask)
			.collect(Collectors.toList());
		System.out.println(results);
	}

	private String longTask(int i) {
		//Simulate long running task
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "result-" + i;
	}

	public static void main(String[] args) {
		SimpleTaskStream sStream = new SimpleTaskStream();
		sStream.testTaskSequence();
	}

}
