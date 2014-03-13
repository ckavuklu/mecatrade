package com.meca.trade.networks;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.meca.trade.to.IMarketData;
import com.meca.trade.to.PriceData;

public class MarketData implements IMarketData {
	Date periodStart = null;
	Date periodEnd = null;
	List<PriceData> priceList = null;
	
	public MarketData(Date periodStart, Date periodEnd,
			List<PriceData> priceList) {
		super();
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.priceList = priceList;
	}

	public void setPriceList(List<PriceData> priceList) {
		this.priceList = priceList;
	}

	@Override
	public Iterator<PriceData> getMarketDataIterator() {
		return priceList.iterator();
	}


	public List<PriceData> getPriceList() {
		return priceList;
	}

	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}

	@Override
	public Date getPeriodStart() {
		// TODO Auto-generated method stub
		return periodStart;
	}

	@Override
	public Date getPeriodEnd() {
		// TODO Auto-generated method stub
		return periodEnd;
	}

}
