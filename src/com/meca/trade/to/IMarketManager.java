package com.meca.trade.to;

import java.util.List;

public interface IMarketManager {
	public Trade executeTrade(Trade trade);
	public Trade realizeTrade(Trade trade);
	public Trade cancelTrade(Trade trade);
}
