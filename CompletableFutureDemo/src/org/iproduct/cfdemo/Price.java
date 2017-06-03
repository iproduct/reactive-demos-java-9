package org.iproduct.cfdemo;

public class Price {
	private Double amount;
	private Currency currency;
	public Price(Double amount, Currency currency) {
		super();
		this.amount = amount;
		this.currency = currency;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public String toString() {
		return amount + " " + currency;
	}
}
