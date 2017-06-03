package org.iproduct.rxjava;
import io.reactivex.*;

public class HelloRxJava {

	public static void main(String[] args) throws InterruptedException {
		Flowable<String> cold = Flowable.just("Hello", "Reactive", "World", "from", "RxJava", "!");
		cold.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		cold.subscribe(i -> System.out.println("Second: " + i));
	}
}
