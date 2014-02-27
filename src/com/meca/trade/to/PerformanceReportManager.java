package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.meca.trade.networks.Parameter;


public class PerformanceReportManager extends MecaObject implements IPerformanceReportManager{

	private IPositionManager positionManager = null;
	private MarketType type = null;
	private String generatedReport;
	private Boolean generateLogReport;
	private Date periodStartDate;
	private Date periodEndDate;
	
	
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
	private IReportLogger  periodBasedPerformanceData = null;


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
		
		periodStartDate = (Date)config.get("PERIOD_START").getValue();
		periodEndDate = (Date)config.get("PERIOD_END").getValue();
		
		this.annualizationCoefficient = 365d * 24d * 60d * 60d * 1000 / (((Date)config.get("PERIOD_END").getValue()).getTime() -  ((Date)config.get("PERIOD_START").getValue()).getTime());
		this.margin = (Double)config.get("ACCOUNT_BALANCE").getValue();
		
		this.graphData = new GraphDataGenerator();
		this.performanceData = new FileReportGenerator();
		this.periodBasedPerformanceData = new FileReportGenerator();
		
	}


	@Override
	public void generatePerformanceReport(IPositionManager positionManager,MarketType type,Boolean generateLogReport) {
		this.positionManager = positionManager;
		this.type = type;
		this.generateLogReport = generateLogReport;
		
		PerformanceKPIS performanceKPIs = new PerformanceKPIS(null,null);
		
		calculatePositionPerformance(null,null);
		evaluatePROM();
		calculateMDM(positionManager.getExecutionHistory());
		calculateStrategyStopLimit();
		
		performanceKPIs.setPeriodStartDate(periodStartDate);
		performanceKPIs.setPeriodEndDate(periodEndDate);
		
		performanceKPIs.setAnnTotalNumberOfLosingTrades(annTotalNumberOfLosingTrades);
		performanceKPIs.setAnnTotalNumberOfWinningTrades(annTotalNumberOfWinningTrades);
		performanceKPIs.setAnnualizedGrossLoss(annualizedGrossLoss);
		performanceKPIs.setAnnualizedGrossProfit(annualizedGrossProfit);
		
		performanceKPIs.setAverageLosingTrade(averageLosingTrade);
		performanceKPIs.setAverageTrade(averageTrade);
		performanceKPIs.setAverageWinningTrade(averageWinningTrade);
		performanceKPIs.setGrossLossForClosedTrades(grossLossForClosedTrades);
		performanceKPIs.setGrossProfitForClosedTrades(grossProfitForClosedTrades);
		performanceKPIs.setLargestLosingTrade(largestLosingTrade);
		performanceKPIs.setLargestWinningTrade(largestWinningTrade);
		performanceKPIs.setMaxConsecutiveLosers(maxConsecutiveLosers);
		performanceKPIs.setMaxConsecutiveWinners(maxConsecutiveWinners);
		performanceKPIs.setMdd(mdd);
		performanceKPIs.setNetProfitForClosedTrades(netProfitForClosedTrades);
		performanceKPIs.setPercentProfitable(percentProfitable);
		performanceKPIs.setProm(prom);
		performanceKPIs.setRatioAverageWinAverageLoss(ratioAverageWinAverageLoss);
		performanceKPIs.setStrategyStopLimit(strategyStopLimit);
		performanceKPIs.setTotalNumberOfEntryTrades(totalNumberOfEntryTrades);
		performanceKPIs.setTotalNumberOfLosingTrades(totalNumberOfLosingTrades);
		performanceKPIs.setTotalNumberOfTrades(totalNumberOfTrades);
		performanceKPIs.setTotalNumberOfWinningTrades(totalNumberOfWinningTrades);
		performanceKPIs.setMddMaxDate(MaxDate);
		performanceKPIs.setMddMinDate(MinDate);
		
		
		
		String headers = performanceKPIs.getHeaders();
		String body = performanceKPIs.getData();
		
		
		String consoleData = performanceKPIs.getPerformanceData();
		
		generatedReport = consoleData;
		
		
		if(Constants.DEBUG_ENABLED){
			System.out.println(headers);
			System.out.println(body);
		}
		
		if(generateLogReport){

			performanceData.writeLog(headers);
			performanceData.writeLog(body);
			
			
			List<PerformanceKPIS> listOfKPIs = generatePeriodicPerformanceReport(periodStartDate, periodEndDate);
			
			if(listOfKPIs.size() > 0){
				periodBasedPerformanceData.writeLog(listOfKPIs.get(0).getHeaders());
				
				for(PerformanceKPIS kpi : listOfKPIs){
					periodBasedPerformanceData.writeLog(kpi.getData());
				}
				
				periodBasedPerformanceData.writeLog(body);
			}
			
			
			
		}
		
	}

	
	
	private List<PerformanceKPIS> generatePeriodicPerformanceReport(Date periodStartDate, Date periodEndDate){
		Date quarterStartDate = TradeUtils.getQuarterStartDate(periodStartDate);
		Date quarterEndDate = TradeUtils.getQuarterEndDate(periodStartDate);
		
		List<PerformanceKPIS> result = new ArrayList<PerformanceKPIS>();
		
		while(quarterStartDate.compareTo(periodEndDate) < 0)
		
		{
			result.add(calculatePeriodBasedPerformanceData(quarterStartDate,quarterEndDate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(quarterEndDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			quarterStartDate = TradeUtils.getQuarterStartDate(cal.getTime());
			quarterEndDate = TradeUtils.getQuarterEndDate(cal.getTime());
		}
		
		return result;
	}
	
	private PerformanceKPIS calculatePeriodBasedPerformanceData(Date startDate, Date endDate){
		PerformanceKPIS performanceKPIs = new PerformanceKPIS(startDate,endDate);
		
		Double periodicNetProfitForClosedTrades = 0d;
		Double periodicGrossProfitForClosedTrades = 0d;
		Double periodicGrossLossForClosedTrades = 0d;
		Integer periodicTotalNumberOfTrades = 0;
		Integer periodicTotalNumberOfWinningTrades = 0;
		Integer periodicTotalNumberOfLosingTrades = 0;
		Integer periodicTotalNumberOfEntryTrades = 0;
		Double periodicLargestWinningTrade = 0d;
		Double periodicLargestLosingTrade = 0d;
		Double periodicPercentProfitable = 0d;
		Double periodicAverageWinningTrade = 0d;
		Double periodicAverageLosingTrade = 0d;
		Double periodicRatioAverageWinAverageLoss = 0d;
		Double periodicAverageTrade = 0d;
		Integer periodicMaxConsecutiveWinners = 0;
		Integer periodicMaxConsecutiveLosers = 0;
		Integer totalNumberOfOpenPositions = 0;
		
		for(IPosition pos:positionManager.getPositions()){
			
			periodicNetProfitForClosedTrades += pos.getRealizedProfitLoss(startDate,endDate);
			periodicGrossProfitForClosedTrades += pos.getRealizedGrossProfit(startDate,endDate);
			periodicGrossLossForClosedTrades += pos.getRealizedGrossLoss(startDate,endDate);
			
			periodicTotalNumberOfTrades += pos.getTotalNumberOfTrades(startDate,endDate);
			periodicTotalNumberOfWinningTrades += pos.getTotalNumberOfWinningTrades(startDate,endDate);
			periodicTotalNumberOfLosingTrades += pos.getTotalNumberOfLosingTrades(startDate,endDate);
			periodicTotalNumberOfEntryTrades += pos.getTotalNumberOfEntryTrades(startDate,endDate);
			
			if(periodicLargestWinningTrade < pos.getLargestWinningTrade(startDate,endDate))
				periodicLargestWinningTrade = pos.getLargestWinningTrade(startDate,endDate);
			
			if(periodicLargestLosingTrade > pos.getLargestLosingTrade(startDate,endDate))
				periodicLargestLosingTrade = pos.getLargestLosingTrade(startDate,endDate);

			
		}
		
		
		periodicPercentProfitable = (periodicTotalNumberOfWinningTrades*1d / ((periodicTotalNumberOfTrades - periodicTotalNumberOfEntryTrades)==0?1:(periodicTotalNumberOfTrades - periodicTotalNumberOfEntryTrades) ))*100;

		periodicAverageWinningTrade = periodicGrossProfitForClosedTrades / (periodicTotalNumberOfWinningTrades==0?1:periodicTotalNumberOfWinningTrades);
		
		periodicAverageLosingTrade = periodicGrossLossForClosedTrades / (periodicTotalNumberOfLosingTrades==0?1:periodicTotalNumberOfLosingTrades);
		
		periodicRatioAverageWinAverageLoss = periodicAverageWinningTrade / (periodicAverageLosingTrade==0?periodicAverageWinningTrade:periodicAverageLosingTrade);
		
		periodicAverageTrade = periodicNetProfitForClosedTrades / ((periodicTotalNumberOfTrades - periodicTotalNumberOfEntryTrades)==0?1:(periodicTotalNumberOfTrades - periodicTotalNumberOfEntryTrades));
		
		totalNumberOfOpenPositions = positionManager.getTotalNumberOfOpenPositionCount(startDate, endDate);
		
		periodicMaxConsecutiveWinners = positionManager.getConsecutiveWinningTrades(startDate,endDate);
		periodicMaxConsecutiveLosers = positionManager.getConsecutiveLosingTrades(startDate,endDate);
		

		performanceKPIs.setAverageLosingTrade(periodicAverageLosingTrade);
		performanceKPIs.setAverageTrade(periodicAverageTrade);
		performanceKPIs.setAverageWinningTrade(periodicAverageWinningTrade);
		performanceKPIs.setGrossLossForClosedTrades(periodicGrossLossForClosedTrades);
		performanceKPIs.setGrossProfitForClosedTrades(periodicGrossProfitForClosedTrades);
		performanceKPIs.setLargestLosingTrade(periodicLargestLosingTrade);
		performanceKPIs.setLargestWinningTrade(periodicLargestWinningTrade);
		performanceKPIs.setMaxConsecutiveLosers(periodicMaxConsecutiveLosers);
		performanceKPIs.setMaxConsecutiveWinners(periodicMaxConsecutiveWinners);
		performanceKPIs.setTotalNumberOfOpenPositions(totalNumberOfOpenPositions);
		
		/*performanceKPIs.setMdd(mdd);*/
		performanceKPIs.setNetProfitForClosedTrades(periodicNetProfitForClosedTrades);
		performanceKPIs.setPercentProfitable(periodicPercentProfitable);
		//performanceKPIs.setProm(prom);
		performanceKPIs.setRatioAverageWinAverageLoss(periodicRatioAverageWinAverageLoss);
		//performanceKPIs.setStrategyStopLimit(strategyStopLimit);
		performanceKPIs.setTotalNumberOfEntryTrades(periodicTotalNumberOfEntryTrades);
		performanceKPIs.setTotalNumberOfLosingTrades(periodicTotalNumberOfLosingTrades);
		performanceKPIs.setTotalNumberOfTrades(periodicTotalNumberOfTrades);
		performanceKPIs.setTotalNumberOfWinningTrades(periodicTotalNumberOfWinningTrades);
		
	
		
		return performanceKPIs;
			
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
	

	
	private void calculatePositionPerformance(Date startDate, Date endDate){
		
		Date intervalDate = null;
		
		for(IPosition pos:positionManager.getPositions()){
			
			/**
			TODO: Where is the quote price
			 
			if(pos.getStatus() == TradeStatusType.OPEN){
				netProfitForOpenTrades += pos.getCurrentProfitLoss(0d);
			}
			*/
			
			netProfitForClosedTrades += pos.getRealizedProfitLoss(startDate,endDate);
			grossProfitForClosedTrades += pos.getRealizedGrossProfit(startDate,endDate);
			grossLossForClosedTrades += pos.getRealizedGrossLoss(startDate,endDate);
			
			totalNumberOfTrades += pos.getTotalNumberOfTrades(startDate,endDate);
			totalNumberOfWinningTrades += pos.getTotalNumberOfWinningTrades(startDate,endDate);
			totalNumberOfLosingTrades += pos.getTotalNumberOfLosingTrades(startDate,endDate);
			totalNumberOfEntryTrades += pos.getTotalNumberOfEntryTrades(startDate,endDate);
			
			if(largestWinningTrade < pos.getLargestWinningTrade(startDate,endDate))
				largestWinningTrade = pos.getLargestWinningTrade(startDate,endDate);
			
			if(largestLosingTrade > pos.getLargestLosingTrade(startDate,endDate))
				largestLosingTrade = pos.getLargestLosingTrade(startDate,endDate);

		}
		
		percentProfitable = (totalNumberOfWinningTrades*1d / ((totalNumberOfTrades - totalNumberOfEntryTrades)==0?1:(totalNumberOfTrades - totalNumberOfEntryTrades) ))*100;

		averageWinningTrade = grossProfitForClosedTrades / (totalNumberOfWinningTrades==0?1:totalNumberOfWinningTrades);
		
		averageLosingTrade = grossLossForClosedTrades / (totalNumberOfLosingTrades==0?1:totalNumberOfLosingTrades);
		
		ratioAverageWinAverageLoss = averageWinningTrade / (averageLosingTrade==0?averageWinningTrade:averageLosingTrade);
		
		averageTrade = netProfitForClosedTrades / ((totalNumberOfTrades - totalNumberOfEntryTrades)==0?1:(totalNumberOfTrades - totalNumberOfEntryTrades));
		
		maxConsecutiveWinners = positionManager.getConsecutiveWinningTrades(startDate,endDate);
		maxConsecutiveLosers = positionManager.getConsecutiveLosingTrades(startDate,endDate);
		
		
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
		performanceData.initializeLogger(name+ "_Performance.xls");
		periodBasedPerformanceData.initializeLogger(name+ "_Periodic_Performance.xls");
	}


	@Override
	public void finalizeLogger() {
		graphData.finalizeLogger();
		performanceData.finalizeLogger();
		periodBasedPerformanceData.finalizeLogger();
	}

	
}
