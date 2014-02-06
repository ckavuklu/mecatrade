package com.meca.trade.to;

import java.util.Date;

public class PriceData extends MecaObject {
	String quote;
	Double volume;
	Date time;
	Double open;
	Double close;
	Double high;
	Double low;
	Double bidPrice;
	Double askPrice;
	
	public Double getVolume() {
		return volume;
	}


	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getQuote() {
		return quote;
	}


	public void setQuote(String quote) {
		this.quote = quote;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


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
		builder.append(" ");
		builder.append("time=");
		builder.append(time);
		
		
		return builder.toString();
	}

	
	public Double getBidPrice() {
		return bidPrice;
	}

	
	public Double getAskPrice() {
		return askPrice;
	}

	public PriceData() {
		super();
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
		setOpen(open);
		setHigh(high);
		setLow(low);
		setClose(close);
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
		
		this.bidPrice = close;
		this.askPrice = bidPrice + Constants.EURUSD_SPREAD;
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
