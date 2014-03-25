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
	PerformanceKPIS performanceKPIs = null;
	
	
	public PerformanceKPIS getPerformanceKPIs() {
		return performanceKPIs;
	}


	//private RunConfiguration config;
	private HashMap<String,Parameter> config;
	
	private Double initialBalance;
	
	private Double annualizationCoefficient = 0d;
	
	private IReportLogger  graphData = null;
	//private IReportLogger  performanceData = null;
	private IReportLogger  periodBasedPerformanceData = null;
	private Double maximumIndicatorWindowSize;
	private Integer numberOfSamples;

	@Override
	public IReportLogger getGraphLogger() {
		
		return graphData;
	}


	public String getGeneratedReport() {
		return generatedReport;
	}

	
	
	
	
	public PerformanceReportManager(IMarketData marketData, Double accountBalance) {
		super();
		
		periodStartDate = marketData.getPeriodStart();
		periodEndDate = marketData.getPeriodEnd();
		
		this.annualizationCoefficient = 365d * 24d * 60d * 60d * 1000 / (periodEndDate.getTime() -  periodStartDate.getTime());
		this.initialBalance = accountBalance;
		this.numberOfSamples = marketData.getSampleSize();
		
		this.graphData = new GraphDataGenerator();
		this.periodBasedPerformanceData = new FileReportGenerator();
		
	}


	@Override
	public void generatePerformanceReport(IPositionManager positionManager,MarketType type,Boolean generateLogReport) {
		this.positionManager = positionManager;
		this.type = type;
		this.generateLogReport = generateLogReport;
		
		performanceKPIs = calculatePeriodBasedPerformanceData(periodStartDate,periodEndDate);
		
		calculateRandomDegreesOfFreedom(performanceKPIs, maximumIndicatorWindowSize,numberOfSamples);
		
		calculateMDM(positionManager.getExecutionHistory(),performanceKPIs);
		evaluatePROM(performanceKPIs);
		calculateRRR(performanceKPIs);
		calculateStrategyStopLimit(performanceKPIs);
		
		String headers = performanceKPIs.getHeaders();
		String body = performanceKPIs.getData();
		
		
		String consoleData = performanceKPIs.getPerformanceData();
		
		generatedReport = consoleData;
		
		if(Constants.DEBUG_ENABLED){
			System.out.println(headers);
			System.out.println(body);
		}
		
		if(generateLogReport){
			
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
		Double periodicStandardErrorOfWinningTrades = 0d;
		
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
		
		periodicStandardErrorOfWinningTrades = positionManager.getStandardErrorOfWinningTrades(startDate,endDate);
		

		performanceKPIs.setStandardErrorOfWinningTrades(periodicStandardErrorOfWinningTrades);
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
	
	private void calculateStrategyStopLimit(PerformanceKPIS kpi) {
		kpi.setStrategyStopLimit((kpi.getMdd()*initialBalance/100d)*Constants.SAFETY_FACTOR);
	}
	
	private void calculateRRR(PerformanceKPIS kpi){
		kpi.setAnnRRR(kpi.getAnnualizedNetProfit() / kpi.getMddValue());
	}

	private void calculateRandomDegreesOfFreedom(PerformanceKPIS kpi, Double maximumIndicatorWindowSize, Integer numberOfSamples){
		kpi.setRandomDegreesOfFreedom(100d*(1-(maximumIndicatorWindowSize/numberOfSamples)));
	}
	
	private void calculateMDM(List<ExecutionRecord> executionHistory, PerformanceKPIS kpi){
		Double mdd = 0d;
		
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
			
			mdd = PctMaxDrawDown;
			kpi.setMddMinDate(minMaxDate);
			kpi.setMddMaxDate(MaxDate);
			
		}
		
		else mdd = Double.NaN;
		
		kpi.setMddValue(Double.valueOf(mdd));
		
		mdd = (mdd / initialBalance)*100d;
		
		kpi.setMdd(mdd);
	}
	

	private void evaluatePROM(PerformanceKPIS performanceKPIs){
		
		Double annualizedGrossProfit = performanceKPIs.getGrossProfitForClosedTrades() * annualizationCoefficient;
		Double annualizedGrossLoss = performanceKPIs.getGrossLossForClosedTrades() * annualizationCoefficient;
		Double annualizedNetProfit = performanceKPIs.getNetProfitForClosedTrades() * annualizationCoefficient;
		
		Double annTotalNumberOfWinningTrades  = performanceKPIs.getTotalNumberOfWinningTrades() * annualizationCoefficient;
		Double annTotalNumberOfLosingTrades = performanceKPIs.getTotalNumberOfLosingTrades() * annualizationCoefficient;
		
		
		Double prom = TradeUtils.roundDownDigits(((((annualizedGrossProfit/(annTotalNumberOfWinningTrades==0d?1:annTotalNumberOfWinningTrades)) * (annTotalNumberOfWinningTrades - Math.sqrt(annTotalNumberOfWinningTrades)))
				 +
				 ((annualizedGrossLoss/(annTotalNumberOfLosingTrades==0d?1:annTotalNumberOfLosingTrades)) * (annTotalNumberOfLosingTrades + Math.sqrt(annTotalNumberOfLosingTrades)))) / initialBalance)*100,2);
		
		performanceKPIs.setProm(prom);
		performanceKPIs.setAnnTotalNumberOfLosingTrades(annTotalNumberOfLosingTrades);
		performanceKPIs.setAnnTotalNumberOfWinningTrades(annTotalNumberOfWinningTrades);
		performanceKPIs.setAnnualizedGrossLoss(annualizedGrossLoss);
		performanceKPIs.setAnnualizedGrossProfit(annualizedGrossProfit);
		performanceKPIs.setAnnualizedNetProfit(annualizedNetProfit);
	}
	
	/*
	 * 
	 * TODO: Currently we return only prom as a sole indicator of fitness value
	 * 
	 * */
	public Double getFitnessValue(){
		return performanceKPIs.getProm();
	}


	@Override
	public void initializeLogger(String name) {
		graphData.initializeLogger(name + "_Graph");
		periodBasedPerformanceData.initializeLogger(name+ "_Periodic_Performance.xls");
	}


	@Override
	public void finalizeLogger() {
		graphData.finalizeLogger();
		periodBasedPerformanceData.finalizeLogger();
	}


	public void setMaximumIndicatorWindowSize(Double maximumIndicatorWindowSize) {
		this.maximumIndicatorWindowSize = maximumIndicatorWindowSize;
	}

	
}
