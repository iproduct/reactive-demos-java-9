package org.iproduct.flowdemo;

import java.util.Random;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowSubscriber<T> implements Subscriber<T> {

	private static final String LOG_FORMAT = "     Subscriber %s >> [%s] %s%n";
	private final Random random = new Random();

	private Subscription subscription;
	private AtomicInteger remaining;

	private String name;
	private int demand = 0;

	public FlowSubscriber(String name) {
		this.name = name;
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		log("Subscribed...");
		this.subscription = subscription;

		request(demand);
	}

	public void setDemand(int n) {
		this.demand = n;
		remaining = new AtomicInteger(demand);
	}

	private void request(int n) {
		log("requesting " + n + " new items...");
		subscription.request(n);
	}

	@Override
	public void onNext(T item) {
		log("item: " + item);

		if (remaining.decrementAndGet() == 0) {
			if (random.nextBoolean()) {
				request(demand);
				remaining.set(demand);
			} else {
				log("Cancel subscription...");
				subscription.cancel();
			}
		}

	}

	@Override
	public void onComplete() {
		log("Complete!");
	}

	@Override
	public void onError(Throwable t) {
		log("Error: " + t.getMessage());
	}

	private void log(String message, Object... args) {
		String fullMessage = String.format(LOG_FORMAT, this.name, Thread.currentThread().getName(), message);
		System.out.printf(fullMessage, args);
	}
}
