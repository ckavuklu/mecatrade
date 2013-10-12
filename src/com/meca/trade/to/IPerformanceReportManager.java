package com.meca.trade.to;

public interface IPerformanceReportManager {
	public void generatePerformanceReport(IPositionManager positionManager,
			IAccountManager accountManager, MarketType type);

}
