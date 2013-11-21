package com.meca.trade.to;

import java.io.Serializable;

public class MarketData extends MecaObject{

	private SchedulingParameter param;
	private String open;
	private String quote;
	private String volume;
	private String low;
	private String close;
	private String high;
	private String date;
	private String time;
	
	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	@Override
	public String toString() {
		return "TradeDataObject(" + param.getPeriod() + ","
				+ param.getPeriodType() + ") [date,time,open,close,high,low] [" + date + ","+ time + ","+ open + "," + close  + "," + high  + "," + low+"]" ;
	}

	public SchedulingParameter getParam() {
		return param;
	}

	public void setParam(SchedulingParameter param) {
		this.param = param;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public MarketData(SchedulingParameter param) {
		super();
		this.param = param;

	}

}
