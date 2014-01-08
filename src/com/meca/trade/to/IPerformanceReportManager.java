package com.meca.trade.to;

public interface IPerformanceReportManager extends ILogger{
	public void generatePerformanceReport(IPositionManager positionManager,MarketType type, Boolean generateLogReport);
	public Double getFitnessValue();
	public String getGeneratedReport();
	public IReportLogger getGraphLogger();

	
}
