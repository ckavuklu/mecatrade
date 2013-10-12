package com.meca.trade.to;

import java.util.List;

public interface IPositionManager {
	
	public Trade addTrade(Trade data);
	
	public List<IPosition> getPositions();
	
	public Double getTotalRisk();
	
	public Integer getConsecutiveWinningTrades();
	
	public Integer getConsecutiveLosingTrades();
	
	public String toString();
	
}
