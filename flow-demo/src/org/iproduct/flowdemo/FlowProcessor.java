package org.iproduct.flowdemo;

import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class FlowProcessor<I, O> extends SubmissionPublisher<O> implements Flow.Processor<I, O> {
	private static final int MAX_BUFFER_CAPACITY = 1024;
	private static final String LOG_FORMAT = "  Processor >> [%s] %s%n";
	private final Function<? super I, ? extends O> function;
	private Subscription subscription;

	FlowProcessor(Executor executor, int maxBufferCapacity, Function<? super I, ? extends O> function) {
		super(executor, maxBufferCapacity);
		this.function = function;
	}

	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		(this.subscription = subscription).request(1);
	}

	@Override
	public void onNext(I item) {
		subscription.request(1);
		O output = function.apply(item);
		log("Transforming: %s --> %s", item, output);
		submit(output);
		
	}

	@Override
	public void onError(Throwable ex) {
		closeExceptionally(ex);
	}

	@Override
	public void onComplete() {
		close();
	}

	private void log(String message, Object... args) {
		String formatted = String.format(LOG_FORMAT, Thread.currentThread().getName(), message);
		System.out.printf(formatted, args);
	}
	
	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger(0);
		Supplier<? extends Integer> supplier = () -> i.incrementAndGet();
		// Supplier<? extends String> supplier = () -> {
		// if( i.get() != 13 )
		// return "result-" + i.incrementAndGet();
		// else
		// throw new RuntimeException("Exception - result can not be 13!");
		// };

		CompletableFuturePublisher<Integer> publisher = new CompletableFuturePublisher<>(ForkJoinPool.commonPool(), MAX_BUFFER_CAPACITY, supplier,
				1, TimeUnit.SECONDS);
		
		FlowProcessor<Integer, String> transformer = 
			new FlowProcessor<>(ForkJoinPool.commonPool(), MAX_BUFFER_CAPACITY, n -> "transformation-result-" + n);
		
		FlowSubscriber<String> subA = new FlowSubscriber<>("A");
		subA.setDemand(5);

		FlowSubscriber<String> subB = new FlowSubscriber<>("B");
		subB.setDemand(10);

		publisher.subscribe(transformer);
		transformer.subscribe(subA);
		transformer.subscribe(subB);

		CompletableFutureFromPublisher<?> complete = new CompletableFutureFromPublisher<>(publisher);
		try {
			complete.join();
		} catch (CompletionException ex) {
			System.out.println("Demo completed with error: " + ex.getMessage());
		}

		System.out.println("Demo finished.");
	}

}
