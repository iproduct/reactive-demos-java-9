package org.iproduct.demo.profiler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Flowable;

public class Profiler {

	private ProcessHandle handle;
	private long pid;
	private long lastInstant;
	private long lastCPUTime;
	private double lastLoad;

	public Profiler() {
		handle = ProcessHandle.current();
		pid = handle.pid();
	}

	public long getPid() {
		return pid;
	}

	public ProcessHandle.Info getInfo() {
		return ProcessHandle.current().info();
	}

	public long getCPUTimeInNanos() {
		return getInfo().totalCpuDuration().get().get(ChronoUnit.NANOS);
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

	public static void main(String[] args) throws InterruptedException {
		Profiler profiler = new Profiler();
		System.out.println(profiler.getPid());
		System.out.println(profiler.getInfo());
		System.out.println(profiler.getInfo().totalCpuDuration().get().get(ChronoUnit.NANOS));
		Flowable.interval(1000, TimeUnit.MILLISECONDS).map(t -> profiler.getJavaCPULoad())
				.subscribe(System.out::println);
		Thread.sleep(100000);

	}

}
