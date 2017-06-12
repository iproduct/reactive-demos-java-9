package org.iproduct.demo.profiler.server.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.iproduct.demo.profiler.cdi.qualifiers.CpuProfiling;
import org.iproduct.demo.profiler.server.model.CpuLoad;
import org.iproduct.demo.profiler.server.model.ProcessInfo;
import org.iproduct.flow.IntervalPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Profiler {
	
	@Inject
	@CpuProfiling
	private Event<CpuLoad> event;
	
	private List<ProcessInfo> oldProcesses;
	
	private Logger logger = LoggerFactory.getLogger(Profiler.class);
	
	public Profiler() {
		logger.info("!!!!!  Profiler bean created successfully.");
	}
	
//	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
//		logger.info("Profiler successfully initialized.");
//    }
// 
//    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
//		logger.info("Profiler destroyed.");
//    }
    
	public Stream<ProcessHandle> getJavaProcessesStream() {
		return ProcessHandle.allProcesses()
				.filter(ph -> ph.info().toString().indexOf("java") >= 0);
	}
	
	public List<ProcessInfo> getJavaProcesses() {
		List<ProcessInfo> allProcesses = getJavaProcessesStream()
				.filter( ph -> ph.info().command().isPresent() )
				.map( ph -> new ProcessInfo(ph.pid(), ph.info().command().get()) )
				.collect(Collectors.toList());
		return allProcesses;
	}
	
	public double getJavaCPULoad() {
		double allTime = getJavaProcessesStream().map(ph -> {
					// System.out.println(info);
					Optional<Duration> time = ph.info().totalCpuDuration();
					return (time.isPresent()) ? time.get().get(ChronoUnit.NANOS) : 0d;
				}).collect(Collectors.summingDouble(time -> time));
		return allTime / 1000000d;
	}
	
	public boolean areProcessesChanged() {
		List<ProcessInfo> newProcesses = getJavaProcesses();
		boolean result = ! newProcesses.equals(oldProcesses);
		oldProcesses = newProcesses;
		return result;
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
					event.fireAsync(new CpuLoad(System.currentTimeMillis(), (int) getJavaCPULoad(), areProcessesChanged()))
						.thenAccept(event -> {
//							logger.info("CPU load event fired: " + event);
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
