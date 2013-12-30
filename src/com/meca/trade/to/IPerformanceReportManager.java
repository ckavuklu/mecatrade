package com.meca.trade.to;

public interface IPerformanceReportManager {
	public void generatePerformanceReport(IPositionManager positionManager,MarketType type);
	public Double getFitnessValue();
	public String getGeneratedReport();

}
