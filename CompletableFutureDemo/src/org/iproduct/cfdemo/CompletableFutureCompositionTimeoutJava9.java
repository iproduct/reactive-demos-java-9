package org.iproduct.cfdemo;

import static org.iproduct.cfdemo.Currency.BGN;
import static org.iproduct.cfdemo.Currency.EUR;
import static org.iproduct.cfdemo.Currency.USD;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class CompletableFutureCompositionTimeoutJava9 {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void testlCompletableFutureComposition() {
		CompletableFuture.supplyAsync(() -> getStockPrice("GOOGL"))
			.thenCombine(CompletableFuture.supplyAsync(() -> getExchangeRate(USD, EUR)), this::convertPrice)
			.orTimeout(1, TimeUnit.SECONDS)
			.exceptionally(throwable -> {
				System.out.println("Error: " + throwable.getMessage());
				return -1d;
			}).whenComplete((price, throwable) -> {
				if (price > 0)
					System.out.println("GOOGL stock price in Euro: " + price);
				else
					System.out.println("Timeout: Service took too long to respond.");
			}).join();
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

	public static void main(String[] args) {
		CompletableFutureCompositionTimeoutJava9 demo = new CompletableFutureCompositionTimeoutJava9();
		demo.testlCompletableFutureComposition();
	}

}
