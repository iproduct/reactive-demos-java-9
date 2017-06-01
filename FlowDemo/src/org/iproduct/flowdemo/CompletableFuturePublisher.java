package org.iproduct.flowdemo;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletableFuturePublisher<T> extends SubmissionPublisher<T> {
	static final int MAX_BUFFER_CAPACITY = 2048;
	private static final String LOG_FORMAT = "Publisher >> [%s] %s%n";
	private final List<CompletableFuture<?>> futuresList;
	private final Random random = new Random();

	CompletableFuturePublisher(Executor executor, int maxBufferCapacity, Supplier<? extends T> supplier, long period,
			TimeUnit unit) {
		super(executor, maxBufferCapacity);

		futuresList = IntStream.range(0, 15).boxed()
//				.map(i -> supplier.get())
				.map(i -> CompletableFuture
						.supplyAsync(supplier, CompletableFuture.delayedExecutor(i, TimeUnit.SECONDS, executor))
//						.supplyAsync(() -> i, CompletableFuture.delayedExecutor(random.nextInt(1000), TimeUnit.MILLISECONDS, executor))
						.whenComplete((item, throwable) -> {
							if (throwable == null) {
								log("publishing item: " + item);
								submit(item);
//								log("estimateMaximumLag: " + super.estimateMaximumLag());
//						        log("estimateMinimumDemand: " + super.estimateMinimumDemand());
							} else {
								log("error: " + throwable);
								closeExceptionally(throwable);
							}
						}))
				.collect(Collectors.toList());

		// Remember to send onComplete to downstream subscribers when all items have
		// been generated
		CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).whenComplete(this::close);
	}

	public void close(Void v, Throwable t) {
		log("Closing...");
		futuresList.forEach(future -> future.cancel(false));
		super.close();
	}

	private void log(String message, Object... args) {
		String fullMessage = String.format(LOG_FORMAT, Thread.currentThread().getName(), message);
		System.out.printf(fullMessage, args);
	}

	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger(0);
		Supplier<? extends String> supplier = () -> "result-" + i.incrementAndGet();
//		Supplier<? extends String> supplier = () -> { 
//			if( i.get() != 13 )	
//				return "result-" + i.incrementAndGet();
//			else 
//				throw new RuntimeException("Exception - result can not be 13!");
//		};
				
		CompletableFuturePublisher<String> publisher = new CompletableFuturePublisher<>(ForkJoinPool.commonPool(),
				MAX_BUFFER_CAPACITY, supplier, 1, TimeUnit.SECONDS);
		FlowSubscriber<String> subA = new FlowSubscriber<>("A");
		subA.setDemand(5);

		FlowSubscriber<String> subB = new FlowSubscriber<>("B");
		subB.setDemand(10);

		publisher.subscribe(subA);
		publisher.subscribe(subB);

		CompletableFutureFromPublisher<String> complete = new CompletableFutureFromPublisher<>(publisher);
		try {
			complete.join();
		} catch (CompletionException ex) {
			System.out.println("Demo completed with error: " + ex.getMessage() );
		}

		System.out.println("Demo finished.");
	}

}
