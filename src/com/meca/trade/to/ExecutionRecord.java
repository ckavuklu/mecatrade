package com.meca.trade.to;

public class ExecutionRecord {

	public PriceData getPriceData() {
		return priceData;
	}

	public Double getEquity() {
		return equity;
	}

	private PriceData priceData;
	private Double equity;
	
	public ExecutionRecord() {
		super();
	}
	
	public ExecutionRecord(PriceData priceData, Double equity) {
		super();
		this.priceData = priceData;
		this.equity = equity;
	}
	

}
