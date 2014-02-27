package com.meca.trade.to;

import java.util.Date;

public interface IPosition extends IPriceData{

	public Double getCurrentRisk();
	
	public Double getOpenLotCount();
	
	public Integer getPositionNo();
	
	public TradeType getTradeType();
	
	public Double getOpenPL();
	
	public Double getRealizedProfitLoss(Date startDate, Date endDate);
	
	public Double getRealizedGrossProfit(Date startDate, Date endDate);
	
	public Double getRealizedGrossLoss(Date startDate, Date endDate);
	
	public Integer getTotalNumberOfTrades(Date startDate, Date endDate);
	
	public Integer getTotalNumberOfWinningTrades(Date startDate, Date endDate);
	
	public Integer getTotalNumberOfLosingTrades(Date startDate, Date endDate);
	
	public Integer getTotalNumberOfEntryTrades(Date startDate, Date endDate);
	
	public Double getLargestWinningTrade(Date startDate, Date endDate);
	
	public Double getLargestLosingTrade(Date startDate, Date endDate);
	
	public Trade addTrade(Trade trade);
	
	public String toString();
	
	public TradeStatusType getStatus();
	
	public Double getEntryPrice();
		
	public Double getStopLoss();

	public Double getTakeProfit();
	
	public Date getEntryDate();
	
	public Date getExitDate();

}
