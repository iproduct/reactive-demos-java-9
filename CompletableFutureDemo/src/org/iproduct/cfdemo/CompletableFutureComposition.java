package org.iproduct.cfdemo;

import static org.iproduct.cfdemo.Currency.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureComposition {

	public void testlCompletableFutureComposition() throws InterruptedException, ExecutionException {
		Double priceInEuro = CompletableFuture.supplyAsync(() -> getStockPrice("GOOGL"))
			.thenCombine(CompletableFuture.supplyAsync(() -> getExchangeRate(USD, EUR)), this::convertPrice)
			.exceptionally(throwable -> {
				System.out.println("Error: " + throwable.getMessage());
				return -1d;
			}).get();
		
		System.out.println("GOOGL stock price in Euro: " + priceInEuro );
	}
	
	public Price getStockPrice(String stockSymbol) {
		switch (stockSymbol) {
		case "GOOGL" : return new Price(950.28, USD);
		case "AAPL" : return new Price(148.96, USD);
		case "MSFT" : return new Price(69.00, USD);
		case "FB" : return new Price(150.24, USD);
		default: 
			throw new RuntimeException("Stocks symbol not found.");
		}
	}
		
	public Double getExchangeRate(Currency from, Currency to) {
		if(from.equals(USD) && to.equals(EUR)) {
			return 1.11815d;
		}
		throw new RuntimeException("Exchange rate not available.");
	}
	
	public Double convertPrice(Price price, Double exchangeRate) {
		return price.getAmount() * exchangeRate;
	}
	

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CompletableFutureComposition demo = new CompletableFutureComposition();
		demo.testlCompletableFutureComposition();
	}

}
