package org.iproduct.cfdemo;

import static org.iproduct.cfdemo.Currency.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletableFutureCompositionTimeout {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void testlCompletableFutureComposition() {
		CompletableFuture.supplyAsync(() -> getStockPrice("GOOGL"))
			.thenCombine(CompletableFuture.supplyAsync(() -> getExchangeRate(USD, EUR)), this::convertPrice)
			.applyToEither(scheduleTimeout(scheduler, 1, TimeUnit.SECONDS), p -> p)
			.exceptionally(throwable -> {
				System.out.println("Error: " + throwable.getMessage());
				return -1d;
			}).whenComplete((price, throwable) -> {
				if (price > 0)
					System.out.println("GOOGL stock price in Euro: " + price);
				else
					System.out.println("Timeout: Service took too long to respond.");
			}).join();
		scheduler.shutdownNow();
	}

	public Price getStockPrice(String stockSymbol) {
		//Simulate long running task
		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		switch (stockSymbol) {
		case "GOOGL":
			return new Price(950.28, USD);
		case "AAPL":
			return new Price(148.96, USD);
		case "MSFT":
			return new Price(69.00, USD);
		case "FB":
			return new Price(150.24, USD);
		default:
			throw new RuntimeException("Stocks symbol not found.");
		}
	}

	public Double getExchangeRate(Currency from, Currency to) {
		if (from.equals(USD) && to.equals(EUR)) {
			return 1.11815d;
		}
		throw new RuntimeException("Exchange rate not available.");
	}

	public Double convertPrice(Price price, Double exchangeRate) {
		return price.getAmount() * exchangeRate;
	}

	public CompletionStage<Double> scheduleTimeout(ScheduledExecutorService scheduler, long delay, TimeUnit unit) {
		CompletableFuture<Double> future = new CompletableFuture<>();
		scheduler.schedule(() -> {
			future.completeExceptionally(new TimeoutException(String.format("Timeout after %s %s", delay, unit)));
		}, delay, unit);
		return future;
	}

	public static void main(String[] args) {
		CompletableFutureCompositionTimeout demo = new CompletableFutureCompositionTimeout();
		demo.testlCompletableFutureComposition();
	}

}
