package com.meca.trade.to;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.meca.trade.networks.Parameter;


public class PerformanceReportManager extends MecaObject implements IPerformanceReportManager{

	private IPositionManager positionManager = null;
	private MarketType type = null;
	private String generatedReport;
	private Boolean generateLogReport;
	
	//private RunConfiguration config;
	private HashMap<String,Parameter> config;
	private Double netProfitForClosedTrades = 0d;
	private Double netProfitForOpenTrades = 0d;
	private Double grossProfitForClosedTrades = 0d;
	private Double grossLossForClosedTrades = 0d;
	private Double largestWinningTrade = 0d;
	private Double largestLosingTrade = 0d;
	
	private Double annualizedGrossProfit;
	private Double annualizedGrossLoss;
	private Double margin;
	
	
	private Double averageWinningTrade = 0d;
	private Double averageLosingTrade = 0d;
	private Double averageTrade = 0d;
	private Double ratioAverageWinAverageLoss = 0d;
	
	private Integer totalNumberOfTrades = 0;
	private Integer totalNumberOfWinningTrades = 0;
	private Integer totalNumberOfEntryTrades= 0;
	private Integer totalNumberOfLosingTrades = 0;
	private Double percentProfitable = 0d;
	
	private Integer maxConsecutiveWinners = 0;
	private Integer maxConsecutiveLosers = 0;
	private Double annualizationCoefficient = 0d;
	private Double prom = 0d;
	private Double annTotalNumberOfWinningTrades  = 0d;
	private Double annTotalNumberOfLosingTrades = 0d;
	
	private IReportLogger  graphData = null;
	private IReportLogger  performanceData = null;

	private Double mdd;
	private Date MaxDate; 
	private Date MinDate;

	private Double strategyStopLimit = 0d;

	@Override
	public IReportLogger getGraphLogger() {
		
		return graphData;
	}


	public String getGeneratedReport() {
		return generatedReport;
	}

	
	public PerformanceReportManager(HashMap<String,Parameter> config) {
		super();
		this.config = config;
		this.annualizationCoefficient = 365d * 24d * 60d * 60d * 1000 / (((Date)config.get("PERIOD_END").getValue()).getTime() -  ((Date)config.get("PERIOD_START").getValue()).getTime());
		this.margin = (Double)config.get("ACCOUNT_BALANCE").getValue();
		
		this.graphData = new GraphDataGenerator();
		this.performanceData = new FileReportGenerator();
		
	}


	@Override
	public void generatePerformanceReport(IPositionManager positionManager,MarketType type,Boolean generateLogReport) {
		this.positionManager = positionManager;
		this.type = type;
		this.generateLogReport = generateLogReport;
		
		
		calculatePositionPerformance();
		evaluatePROM();
		calculateMDM(positionManager.getExecutionHistory());
		calculateStrategyStopLimit();
		
		StringBuilder builder = new StringBuilder();
		builder.append("PerformanceReportManager.generatePerformanceReport()");
		builder.append(Constants.ENDLN);
		builder.append("inputMarketDataFileName=");
		builder.append(config.get("INPUT_MARKET_DATA_FILE_NAME").getValue());
		builder.append(Constants.FORMAT);
		builder.append("inputTestTradeDataFileName=");
		builder.append(config.get("INPUT_TEST_TRADE_DATA_FILE_NAME").getValue());
		builder.append(Constants.FORMAT);
		builder.append("netProfitForClosedTrades=");
		builder.append(netProfitForClosedTrades);
		builder.append(Constants.FORMAT);
		builder.append("grossProfitForClosedTrades=");
		builder.append(grossProfitForClosedTrades);
		builder.append(Constants.FORMAT);
		builder.append("grossLossForClosedTrades=");
		builder.append(grossLossForClosedTrades);
		builder.append(Constants.FORMAT);
		builder.append("totalNumberOfTrades=");
		builder.append(totalNumberOfTrades);
		builder.append(Constants.FORMAT);
		builder.append("totalNumberOfWinningTrades=");
		builder.append(totalNumberOfWinningTrades);
		builder.append(Constants.FORMAT);
		builder.append("totalNumberOfLosingTrades=");
		builder.append(totalNumberOfLosingTrades);
		builder.append(Constants.FORMAT);
		builder.append("totalNumberOfEntryTrades=");
		builder.append(totalNumberOfEntryTrades);
		builder.append(Constants.FORMAT);
		builder.append("percentProfitable=");
		builder.append(percentProfitable);
		builder.append(Constants.FORMAT);
		builder.append("largestWinningTrade=");
		builder.append(largestWinningTrade);
		builder.append(Constants.FORMAT);
		builder.append("largestLosingTrade=");
		builder.append(largestLosingTrade);
		builder.append(Constants.FORMAT);
		builder.append("averageWinningTrade=");
		builder.append(averageWinningTrade);
		builder.append(Constants.FORMAT);
		builder.append("averageLosingTrade=");
		builder.append(averageLosingTrade);
		builder.append(Constants.FORMAT);
		builder.append("ratioAverageWinAverageLoss=");
		builder.append(ratioAverageWinAverageLoss);
		builder.append(Constants.FORMAT);
		builder.append("averageTrade=");
		builder.append(averageTrade);
		builder.append(Constants.FORMAT);
		builder.append("maxConsecutiveWinners=");
		builder.append(maxConsecutiveWinners);
		builder.append(Constants.FORMAT);
		builder.append("maxConsecutiveLosers=");
		builder.append(maxConsecutiveLosers);
		builder.append(Constants.FORMAT);
		builder.append("PROM=");
		builder.append(prom);
		builder.append(Constants.FORMAT);
		builder.append("annTotalNumberOfWinningTrades=");
		builder.append(annTotalNumberOfWinningTrades);
		builder.append(Constants.FORMAT);
		builder.append("annTotalNumberOfLosingTrades=");
		builder.append(annTotalNumberOfLosingTrades);
		builder.append(Constants.FORMAT);
		builder.append("annualizedGrossProfit=");
		builder.append(annualizedGrossProfit);
		builder.append(Constants.FORMAT);
		builder.append("annualizedGrossLoss=");
		builder.append(annualizedGrossLoss);
		builder.append(Constants.FORMAT);
		builder.append("MDD(%)=");
		builder.append(mdd);
		builder.append(Constants.FORMAT);
		builder.append("strategyStopLimit(%)=");
		builder.append(strategyStopLimit);
		
		
		
		builder.append(Constants.FORMAT);

		if (MaxDate != null) {
			builder.append("MaxDate=");
			builder.append(MaxDate.toString());
			builder.append(Constants.FORMAT);
			builder.append("MinDate=");
			builder.append(MinDate.toString());
			builder.append(Constants.FORMAT);
		}
		
		builder.append(Constants.ENDLN);
		
		generatedReport = builder.toString();
		
		if(Constants.DEBUG_ENABLED)
			System.out
				.println(builder.toString());
		
		if(generateLogReport){
			performanceData.writeLog(generatedReport);
		}
		
	}
	
	private void calculateStrategyStopLimit() {
		this.strategyStopLimit = (mdd*margin/100d)*Constants.SAFETY_FACTOR; 	
	}


	private void calculateMDM(List<ExecutionRecord> executionHistory){
		
		
		if (executionHistory.size()>0){
			
			Double Max = 0d;
			Date MaxDateTemp = null;
			Date MaxDate = null;
			Date minMaxDate = null;
			
			Double PctMaxDrawDown = 0d;
			
			for(ExecutionRecord record:executionHistory){
				
				if (record.getEquity() > Max) 
				{
					Max = record.getEquity();
					MaxDateTemp = record.getPriceData().getTime();
				}
				else 
				{
					Double drawdown = (Max-record.getEquity());
					
					if (drawdown > PctMaxDrawDown) {
						PctMaxDrawDown = drawdown;
						minMaxDate = record.getPriceData().getTime();
						MaxDate = MaxDateTemp;
					}
					
				}
					
			}
			
			this.mdd = PctMaxDrawDown;
			this.MinDate = minMaxDate;
			this.MaxDate = MaxDate;
			
		}
		
		else this.mdd = Double.NaN;
		
		
		this.mdd = (this.mdd / margin)*100d;
		
	}
	

	
	private void calculatePositionPerformance(){
		
		for(IPosition pos:positionManager.getPositions()){
			
			/**
			TODO: Where is the quote price
			 
			if(pos.getStatus() == TradeStatusType.OPEN){
				netProfitForOpenTrades += pos.getCurrentProfitLoss(0d);
			}
			*/
			
			netProfitForClosedTrades += pos.getRealizedProfitLoss();
			grossProfitForClosedTrades += pos.getRealizedGrossProfit();
			grossLossForClosedTrades += pos.getRealizedGrossLoss();
			
			totalNumberOfTrades += pos.getTotalNumberOfTrades();
			totalNumberOfWinningTrades += pos.getTotalNumberOfWinningTrades();
			totalNumberOfLosingTrades += pos.getTotalNumberOfLosingTrades();
			totalNumberOfEntryTrades += pos.getTotalNumberOfEntryTrades();
			
			if(largestWinningTrade < pos.getLargestWinningTrade())
				largestWinningTrade = pos.getLargestWinningTrade();
			
			if(largestLosingTrade > pos.getLargestLosingTrade())
				largestLosingTrade = pos.getLargestLosingTrade();

		}
		
		percentProfitable = (totalNumberOfWinningTrades*1d / (totalNumberOfTrades - totalNumberOfEntryTrades) )*100;

		averageWinningTrade = grossProfitForClosedTrades / (totalNumberOfWinningTrades==0?1:totalNumberOfWinningTrades);
		
		averageLosingTrade = grossLossForClosedTrades / (totalNumberOfLosingTrades==0?1:totalNumberOfLosingTrades);
		
		ratioAverageWinAverageLoss = averageWinningTrade / (averageLosingTrade==0?averageWinningTrade:averageLosingTrade);
		
		averageTrade = netProfitForClosedTrades / (totalNumberOfTrades - totalNumberOfEntryTrades);
		
		maxConsecutiveWinners = positionManager.getConsecutiveWinningTrades();
		maxConsecutiveLosers = positionManager.getConsecutiveLosingTrades();
		
		
	}
	
	private void evaluatePROM(){
		
		annualizedGrossProfit = grossProfitForClosedTrades * annualizationCoefficient;
		annualizedGrossLoss = grossLossForClosedTrades * annualizationCoefficient;
		
		annTotalNumberOfWinningTrades  = totalNumberOfWinningTrades * annualizationCoefficient;
		annTotalNumberOfLosingTrades = totalNumberOfLosingTrades * annualizationCoefficient;
		
		
		prom = TradeUtils.roundDownDigits(((((annualizedGrossProfit/(annTotalNumberOfWinningTrades==0d?1:annTotalNumberOfWinningTrades)) * (annTotalNumberOfWinningTrades - Math.sqrt(annTotalNumberOfWinningTrades)))
				 +
				 ((annualizedGrossLoss/(annTotalNumberOfLosingTrades==0d?1:annTotalNumberOfLosingTrades)) * (annTotalNumberOfLosingTrades + Math.sqrt(annTotalNumberOfLosingTrades)))) / margin)*100,2);
		
		return;
	}
	
	/*
	 * 
	 * TODO: Currently we return only prom as a sole indicator of fitness value
	 * 
	 * */
	public Double getFitnessValue(){
		return prom;
	}


	@Override
	public void initializeLogger(String name) {
		graphData.initializeLogger(name + "_Graph");
		performanceData.initializeLogger(name+ "_Performance");
	}


	@Override
	public void finalizeLogger() {
		graphData.finalizeLogger();
		performanceData.finalizeLogger();
	}

	
}
