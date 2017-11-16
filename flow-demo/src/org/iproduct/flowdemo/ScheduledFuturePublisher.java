package org.iproduct.flowdemo;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ScheduledFuturePublisher<T> extends SubmissionPublisher<T> {
	private static final String LOG_FORMAT = "Publisher >> [%s] %s%n";
	static final int MAX_BUFFER_CAPACITY = 256;
	static final int MAX_ITEMS = 9;
	final ScheduledFuture<?> periodicTask;
	final ScheduledExecutorService scheduler;
	

	ScheduledFuturePublisher(Executor executor, int maxBufferCapacity,
                     Supplier<? extends T> supplier,
                     long period, TimeUnit unit) {
		super(executor, maxBufferCapacity);
	     scheduler = new ScheduledThreadPoolExecutor(1);
	     periodicTask = scheduler.scheduleAtFixedRate(
	       () -> {
	    	   T item = supplier.get();
	    	   log("publishing item: " + item);
	    	   submit(item);
	    	   if(item.equals("result-" + MAX_ITEMS)) {
	    		   close();
	    	   }
	       }, 0, period, unit);
   }

	public void close() {
		log("Closing...");
		periodicTask.cancel(false);
		scheduler.shutdown();
		super.close();
		log("Demo finished.");
	}

	private void log(String message, Object... args) {
		String fullMessage = String.format(LOG_FORMAT, Thread.currentThread().getName(), message);
		System.out.printf(fullMessage, args);
	}
    
	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger(0);
		Supplier<? extends String> supplier = () -> "result-" + i.incrementAndGet();
		ScheduledFuturePublisher<String> publisher = 
			new ScheduledFuturePublisher<>(ForkJoinPool.commonPool(),  MAX_BUFFER_CAPACITY, supplier, 1, TimeUnit.SECONDS);
		FlowSubscriber<String> subA = new FlowSubscriber<>("A");
        subA.setDemand(5);
 
        FlowSubscriber<String> subB = new FlowSubscriber<>("B");
        subB.setDemand(10);
 
        publisher.subscribe(subA);
        publisher.subscribe(subB);
	}

}
