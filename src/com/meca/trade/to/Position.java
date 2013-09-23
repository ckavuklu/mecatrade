package com.meca.trade.to;

import java.util.List;


public class Position extends MecaObject implements IPosition {

	private Long positionNo;
	private TradeStatusType status;
	private List<Trade> tradeList;
	private MarketType market;
	
	public Double getCurrentRisk() { return null;}
	
	public Double getCurrentProfitLoss() { return null;}
	
}
