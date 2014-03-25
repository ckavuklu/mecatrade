package com.meca.trade.to;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public interface IMarketData {
	
	public Date getPeriodStart();

	public Date getPeriodEnd();

	public Iterator<PriceData> getMarketDataIterator();
	
	public Integer getSampleSize();
	
}
