package org.iproduct.cfdemo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletableFutureStream {
	private ExecutorService executor = ForkJoinPool.commonPool();

//	private ExecutorService executor = Executors.newCachedThreadPool();

	public void testlCompletableFutureSequence() {
		List<CompletableFuture<String>> futuresList = IntStream.range(0, 20)
			.boxed()
			.map(i -> longCompletableFutureTask(i, executor).exceptionally(t -> t.getMessage()))
			.collect(Collectors.toList());
		
		CompletableFuture<List<String>> results = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]))
		   	.thenApply(v -> futuresList.stream()
   				.map(CompletableFuture::join)
   				.collect(Collectors.toList())
		   	);
		try {
			System.out.println(results.get(10, TimeUnit.SECONDS));
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}
	
	private CompletableFuture<String> longCompletableFutureTask(int i, Executor executor) {
		
		return CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			if (i == 13) {
//				throw new RuntimeException("Exception - result can not be 13!");
//			}
			return i + "-" + "test";
		}, executor);
	}

	public static void main(String[] args) {
		CompletableFutureStream cfStream = new CompletableFutureStream();
		cfStream.testlCompletableFutureSequence();
	}

}
