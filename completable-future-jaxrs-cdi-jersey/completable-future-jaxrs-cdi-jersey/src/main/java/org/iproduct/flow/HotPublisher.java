package org.iproduct.flow;

import java.util.concurrent.Flow.Publisher;

public interface HotPublisher<T> extends Publisher<T>{
	void cancel(boolean mayInterruptIfRunning);
	void join();
}
