package com.meca.trade.to;

public interface IReportLogger {
	public void initializeLogger(String name);
	public void writeGraphLog(PriceData priceData,Double equity, Double margin,Double freeMargin,Double marginLevel,Double openPL);
	public void writeStringLog(String log);
	public void finalizeLogger();
	
}
