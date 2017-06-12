package org.iproduct.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class IntervalPublisher<T> extends SubmissionPublisher<T> implements HotPublisher<T> {
	public static final int DEFAULT_MAX_BUFFER_CAPACITY = 256;
	private volatile CompletableFuture<Void> finished = new CompletableFuture<Void>();
	private Supplier<? extends T> supplier;
	private Executor executor; 
	private long period;
	private TimeUnit unit;
	private boolean wasActivated;
	private Function<Throwable, Void> onerror = throwable ->  { 
		closeExceptionally(throwable); 
		finished.completeExceptionally(throwable);
		return null; 
	};
	
	public static HotPublisher<Integer> getDefaultIntervalPublisher(long period, TimeUnit unit) {
		AtomicInteger i = new AtomicInteger(0);
		return new IntervalPublisher<Integer>(i::incrementAndGet, period, unit);
	}
	
	public IntervalPublisher(Supplier<? extends T> supplier, long delay, long period, TimeUnit unit, Executor executor, int maxBufferCapacity) {
		super(executor, maxBufferCapacity);
		this.supplier = supplier;
		this.executor = executor;
		this.period = period;
		this.unit = unit;
		
		CompletableFuture
			.supplyAsync(supplier, CompletableFuture.delayedExecutor(delay, unit, executor))
			.thenAccept(this::onCompletion)
			.exceptionally( onerror );
	}
	
	public IntervalPublisher(Supplier<? extends T> supplier, long delay, long period, TimeUnit unit, Executor executor) {
		this(supplier, delay, period, unit, executor, DEFAULT_MAX_BUFFER_CAPACITY);
	}

	public IntervalPublisher(Supplier<? extends T> supplier, long delay, long period, TimeUnit unit) {
		this(supplier, delay, period, unit, ForkJoinPool.commonPool());
	}
	
	public IntervalPublisher(Supplier<? extends T> supplier, long period, TimeUnit unit) {
		this(supplier, 0, period, unit);
	}
	
	private void onCompletion(T item) {
		if(hasSubscribers()) {
			wasActivated = true;
			submit(item);
		} else if(wasActivated) {
			close();
			return;
		}
		
		CompletableFuture
			.supplyAsync(supplier, CompletableFuture.delayedExecutor(period, unit, executor))
			.thenAccept(this::onCompletion)
			.exceptionally( onerror );
	}
	
	@Override
	public void close() {
//		System.out.println("Closing...");
		finished.complete(null);
		super.close();
	}

	public void cancel(boolean mayInterruptIfRunning) {
		finished.cancel(mayInterruptIfRunning);
	};
	
	public void join() {
		finished.join();
	}
	
	public static void main(String[] args) {

		HotPublisher<Integer> publisher = IntervalPublisher.getDefaultIntervalPublisher(1, TimeUnit.SECONDS);

		FlowSubscriber<Integer> subA = new FlowSubscriber<>("A");
		subA.setDemand(2);

		FlowSubscriber<Integer> subB = new FlowSubscriber<>("B");
		subB.setDemand(5);

		publisher.subscribe(subA);
		publisher.subscribe(subB);
		
		// when all subscribers have unsubscribed - finish
		publisher.join();

		System.out.println("Demo finished.");
	}

}
