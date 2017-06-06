package org.iproduct.cfdemo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletableFutureDelayedStream {
//	private ExecutorService executor = ForkJoinPool.commonPool();

//	private ExecutorService executor = Executors.newCachedThreadPool();

	public void testlCompletableFutureSequence() {
		List<CompletableFuture<String>> futuresList = IntStream.range(0, 20)
			.boxed()
			.map(i -> 
				completableFutureTask(i, CompletableFuture.delayedExecutor(i, TimeUnit.SECONDS))
					.exceptionally(t -> t.getMessage())
					.whenComplete((result, exception) -> { 
						System.out.println(result);
					})
			).collect(Collectors.toList());
		
		CompletableFuture<List<String>> results = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]))
		   	.thenApply(v -> futuresList.stream()
   				.map(CompletableFuture::join)
   				.collect(Collectors.toList())
		   	);
			System.out.println(results.join());
	}
	
	private CompletableFuture<String> completableFutureTask(int i, Executor executor) {
		
		return CompletableFuture.supplyAsync(() -> {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			if (i == 13) {
//				throw new RuntimeException("Exception - result can not be 13!");
//			}
			return i + "-" + "test";
		}, executor);
	}

	public static void main(String[] args) {
		CompletableFutureDelayedStream cfStream = new CompletableFutureDelayedStream();
		cfStream.testlCompletableFutureSequence();
	}

}
