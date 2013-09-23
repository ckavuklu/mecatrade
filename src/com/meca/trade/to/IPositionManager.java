package com.meca.trade.to;

import java.util.List;

public interface IPositionManager {
	public Trade addTrade(Trade data);
	public List<IPosition> getPositions();
	public Double getTotalRisk();
}
