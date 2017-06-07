package org.iproduct.demo.profiler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.iproduct.demo.profiler.cdi.qualifiers.CpuProfiling;
import org.iproduct.flow.IntervalPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Profiler {
	
	@Inject
	@CpuProfiling
	private Event<CpuLoad> event;
	
	private Logger logger = LoggerFactory.getLogger(Profiler.class);
	
	public Profiler() {
		logger.info("!!!!!  Profiler bean created successfully.");
	}
	
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		logger.info("Profiler successfully initialized.");
    }
 
    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
		logger.info("Profiler destroyed.");
    }

	public double getJavaCPULoad() {
		double allTime = ProcessHandle.allProcesses().map(ph -> ph.info())
				.filter(info -> info.toString().indexOf("java") >= 0).map(info -> {
					// System.out.println(info);
					Optional<Duration> time = info.totalCpuDuration();
					return (time.isPresent()) ? time.get().get(ChronoUnit.NANOS) : 0d;
				}).collect(Collectors.summingDouble(time -> time));
		return allTime / 1000000d;
	}
	
	@PostConstruct
	public void subscribeForCpuEvents() {
		IntervalPublisher.getDefaultIntervalPublisher(500, TimeUnit.MILLISECONDS)
			.subscribe(new Subscriber<Integer>() {

				@Override
				public void onComplete() {}

				@Override
				public void onError(Throwable t) {}

				@Override
				public void onNext(Integer i) {
					event.fireAsync(new CpuLoad(System.currentTimeMillis(), (int) getJavaCPULoad()))
						.thenAccept(event -> {
							logger.info("CPU load event fired: " + event);
						});
				}

				@Override
				public void onSubscribe(Subscription subscription) {
					subscription.request(Long.MAX_VALUE);
				}
			});
	}

	public static void main(String[] args) throws InterruptedException {
//		Profiler profiler = new Profiler();
//		Flowable.interval(1000, TimeUnit.MILLISECONDS).map(t -> profiler.getJavaCPULoad())
//				.subscribe(System.out::println);
//		Thread.sleep(100000);

	}

}
