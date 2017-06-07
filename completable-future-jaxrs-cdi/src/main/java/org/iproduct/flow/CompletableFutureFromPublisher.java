package org.iproduct.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class CompletableFutureFromPublisher<T> extends CompletableFuture<T> {
	private Subscription subscription;

	public CompletableFutureFromPublisher(Publisher<T> publisher) {
		publisher.subscribe(new Subscriber<>() {

			@Override
			public void onComplete() {
				CompletableFutureFromPublisher.this.complete(null);
			}

			@Override
			public void onError(Throwable t) {
				CompletableFutureFromPublisher.this.completeExceptionally(t);
			}

			@Override
			public void onNext(T item) {
			}

			@Override
			public void onSubscribe(Subscription subscription) {
				CompletableFutureFromPublisher.this.subscription = subscription;
				subscription.request(Integer.MAX_VALUE);
			}
		});
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		subscription.cancel();
		return super.cancel(mayInterruptIfRunning);
	}
}
