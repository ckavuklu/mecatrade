package com.meca.trade.to;

import java.util.List;

public interface IPosition extends IPriceData{

	public Double getCurrentRisk();
	
	public Double getOpenLotCount();
	
	public Integer getPositionNo();
	
	public TradeType getTradeType();
	
	public Double getOpenPL();
	
	public Double getRealizedProfitLoss();
	
	public Double getRealizedGrossProfit();
	
	public Double getRealizedGrossLoss();
	
	public Integer getTotalNumberOfTrades();
	
	public Integer getTotalNumberOfWinningTrades();
	
	public Integer getTotalNumberOfLosingTrades();
	
	public Integer getTotalNumberOfEntryTrades();
	
	public Double getLargestWinningTrade();
	
	public Double getLargestLosingTrade();
	
	public Trade addTrade(Trade trade);
	
	public String toString();
	
	public TradeStatusType getStatus();
	
	public Double getEntryPrice();
		
	public Double getStopLoss();

	public Double getTakeProfit();

}
