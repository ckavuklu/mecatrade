package com.meca.trade.to;


public class PerformanceReportManager extends MecaObject implements IPerformanceReportManager{

	private IPositionManager positionManager = null;
	private IAccountManager accountManager = null;
	private MarketType type = null;
	
	private String inputMarketDataFileName = null;
	private String inputTestTradeDataFileName = null;
	
	private Double netProfitForClosedTrades = 0d;
	private Double netProfitForOpenTrades = 0d;
	private Double grossProfitForClosedTrades = 0d;
	private Double grossLossForClosedTrades = 0d;
	private Double largestWinningTrade = 0d;
	private Double largestLosingTrade = 0d;
	
	private Double averageWinningTrade = 0d;
	private Double averageLosingTrade = 0d;
	private Double averageTrade = 0d;
	private Double ratioAverageWinAverageLoss = 0d;
	
	private Integer totalNumberOfTrades = 0;
	private Integer totalNumberOfWinningTrades = 0;
	private Integer totalNumberOfEntryTrades= 0;
	private Integer totalNumberOfLosingTrades = 0;
	private Double percentProfitable = 0d;
	private final String endln = "\r\n";
	private final String format = endln;
	private Integer maxConsecutiveWinners = 0;
	private Integer maxConsecutiveLosers = 0;
	


	public PerformanceReportManager(String inputMarketDataFileName,
			String inputTestTradeDataFileName) {
		super();
		this.inputMarketDataFileName = inputMarketDataFileName;
		this.inputTestTradeDataFileName = inputTestTradeDataFileName;
	}


	@Override
	public void generatePerformanceReport(IPositionManager positionManager,
			IAccountManager accountManager, MarketType type) {
		this.positionManager = positionManager;
		this.accountManager = accountManager;
		this.type = type;
		
		
		calculatePositionPerformance();
		StringBuilder builder = new StringBuilder();
		builder.append("PerformanceReportManager.generatePerformanceReport()");
		builder.append(endln);
		builder.append("inputMarketDataFileName=");
		builder.append(inputMarketDataFileName);
		builder.append(format);
		builder.append("inputTestTradeDataFileName=");
		builder.append(inputTestTradeDataFileName);
		builder.append(format);
		builder.append("netProfitForClosedTrades=");
		builder.append(netProfitForClosedTrades);
		builder.append(format);
		builder.append("grossProfitForClosedTrades=");
		builder.append(grossProfitForClosedTrades);
		builder.append(format);
		builder.append("grossLossForClosedTrades=");
		builder.append(grossLossForClosedTrades);
		builder.append(format);
		builder.append("totalNumberOfTrades=");
		builder.append(totalNumberOfTrades);
		builder.append(format);
		builder.append("totalNumberOfWinningTrades=");
		builder.append(totalNumberOfWinningTrades);
		builder.append(format);
		builder.append("totalNumberOfLosingTrades=");
		builder.append(totalNumberOfLosingTrades);
		builder.append(format);
		builder.append("totalNumberOfEntryTrades=");
		builder.append(totalNumberOfEntryTrades);
		builder.append(format);
		builder.append("percentProfitable=");
		builder.append(percentProfitable);
		builder.append(format);
		builder.append("largestWinningTrade=");
		builder.append(largestWinningTrade);
		builder.append(format);
		builder.append("largestLosingTrade=");
		builder.append(largestLosingTrade);
		builder.append(format);
		builder.append("averageWinningTrade=");
		builder.append(averageWinningTrade);
		builder.append(format);
		builder.append("averageLosingTrade=");
		builder.append(averageLosingTrade);
		builder.append(format);
		builder.append("ratioAverageWinAverageLoss=");
		builder.append(ratioAverageWinAverageLoss);
		builder.append(format);
		builder.append("averageTrade=");
		builder.append(averageTrade);
		builder.append(format);
		builder.append("maxConsecutiveWinners=");
		builder.append(maxConsecutiveWinners);
		builder.append(format);
		builder.append("maxConsecutiveLosers=");
		builder.append(maxConsecutiveLosers);
		builder.append(format);
		
		builder.append(endln);
		
		System.out
				.println(builder.toString());
		
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
	

}
