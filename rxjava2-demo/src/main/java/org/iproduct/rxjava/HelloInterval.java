package org.iproduct.rxjava;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class HelloInterval {

	public static void main(String[] args) throws InterruptedException {
		Flowable<Long> cold = Flowable.intervalRange(1,10,0,200, TimeUnit.MILLISECONDS);
		cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		cold.subscribe(i -> System.out.println("Second: " + i));
		Thread.sleep(2000);
	}
}
