package org.iproduct.rxjava;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;

public class HelloIntervalHot {

	public static void main(String[] args) throws InterruptedException {
		ConnectableFlowable<Long> hot = Flowable.intervalRange(1,10,0,200, TimeUnit.MILLISECONDS).publish();;
		
		// Instructs the ConnectableObservable to begin emitting the items 
		// from its underlying Flowable to its Subscribers. 
		hot.connect(); 
		
		hot.subscribe(i -> System.out.println("First: " + i));
		Thread.sleep(500);
		hot.subscribe(i -> System.out.println("Second: " + i));	
		Thread.sleep(2000);
	}
}
