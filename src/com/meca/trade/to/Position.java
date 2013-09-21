package com.meca.trade.to;

import java.util.List;


public class Position {

	private Long positionNo;
	private TradeStatusType status;
	private List<Trade> tradeList;
	private MarketType market;
	
	private Double getCurrentRisk() { return null;}
	
	private Double getCurrentProfitLoss() { return null;}
	
	
	
}
