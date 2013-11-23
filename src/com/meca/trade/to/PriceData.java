package com.meca.trade.to;

public class PriceData extends MecaObject {
	
	Double open;
	Double close;
	Double high;
	Double low;
	Double bidPrice;
	Double askPrice;
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("open=");
		builder.append(open);
		builder.append(" ");
		builder.append("close=");
		builder.append(close);
		builder.append(" ");
		builder.append("high=");
		builder.append(high);
		builder.append(" ");
		builder.append("low=");
		builder.append(low);
		builder.append(" ");
		builder.append("bidPrice=");
		builder.append(bidPrice);
		builder.append(" ");
		builder.append("askPrice=");
		builder.append(askPrice);
		
		
		return builder.toString();
	}

	
	public Double getBidPrice() {
		return bidPrice;
	}

	
	public Double getAskPrice() {
		return askPrice;
	}

	

	/**
	 * 
	 * @param open open price of quote
	 * @param close
	 * @param high
	 * @param low
	 */
	public PriceData(Double open, Double close, Double high, Double low) {
		super();
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.bidPrice = close;
		this.askPrice = bidPrice + Constants.EURUSD_SPREAD;
	}

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}
	
}
