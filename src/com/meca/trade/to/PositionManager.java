package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class PositionManager extends MecaObject implements IPositionManager{

	
	private List<IPosition> positionList;
	private List<Trade> tradeHistory;
	private PriceData priceData;
	private Double equity = 0d;
	private Double margin = 0d;
	private Double freeMargin = 0d;
	private Double marginLevel = 100d;
	private Double openPL = 0d;
	private IAccount account;
	private MarketType marketType;
	private Double marginLotCount = 0d;
	private Double weightedAverageEntryPrice = 0d;
	private Boolean graphLog = false;
	private Boolean isHeadersWritten = false;
	



	private String uuid;
	private List<ExecutionRecord> executionHistory = null; 
	private Double initialBalance = null;
	
	public Boolean getIsHeadersWritten() {
		return isHeadersWritten;
	}
	
	public List<ExecutionRecord> getExecutionHistory() {
		return executionHistory;
	}

	public Double getBalance(){
		return account.getBalance();
	}


	public void setGraphLog(Boolean graphLog) {
		this.graphLog = graphLog;
		
		if(graphLog){
			perfReporManager.initializeLogger(uuid);
		}
	}


	public Integer getOpenPositionCount(TradeType tradeType){
		Integer result = 0;
		for(IPosition pos:positionList){
			if(pos.getStatus() == TradeStatusType.OPEN)
				if(pos.getTradeType() == tradeType)
					result += 1;
		}
		return result;
	}

	public Double getMarginLotCount() {
		Double result = 0d;
		for(IPosition pos:positionList){
			if(pos.getStatus() == TradeStatusType.OPEN){
				
				if(pos.getTradeType() == TradeType.BUY)
					result += pos.getOpenLotCount();
				else
					result -= pos.getOpenLotCount();
			}
		}
		
		marginLotCount = Math.abs(result);
		
		return marginLotCount;
	}
	
	
	

	public Double getWeightedAverageEntryPrice() {
		Double result = 0d;
		Double totalLotCount = 0d;
		
		for(IPosition pos:positionList){
			if(pos.getStatus() == TradeStatusType.OPEN){
				totalLotCount += pos.getOpenLotCount();
				result +=  pos.getOpenLotCount() * pos.getEntryPrice();
			}
		}

		weightedAverageEntryPrice = (totalLotCount >0 )?(result / totalLotCount):0d;
		
		return weightedAverageEntryPrice;
	}

	public IAccount getAccount() {
		return account;
	}



	private IPerformanceReportManager perfReporManager;
	

	public List<IPosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<IPosition> positionList) {
		this.positionList = positionList;
	}

	public List<Trade> getTradeHistory() {
		return tradeHistory;
	}

	public PriceData getPriceData() {
		return priceData;
	}

	public Double getEquity() {
		return equity;
	}

	public Double getMargin() {
		return margin;
	}

	public Double getFreeMargin() {
		return freeMargin;
	}

	public Double getMarginLevel() {
		return marginLevel;
	}

	@Override
	public void updatePriceData(PriceData data) {
		this.priceData = data;
		
		for(IPosition pos:positionList){
			pos.updatePriceData(priceData);
		}
		
		update();

	}

	private void updateFreeMargin() {
		freeMargin = TradeUtils.getRoundedUpValue(getEquity() - getMargin());
	}

	private void updateEquity() {
		equity = TradeUtils.getRoundedUpValue(account.getBalance() + getOpenPL());
	}

	private String getGraphChartData(String delimitChar){
		StringBuilder builder = new StringBuilder();
		builder.append(getStringRepresentationOfPositionVariables(false,";"));
		
		if(priceData!=null){
			builder.append(priceData.getOpen());
			builder.append(delimitChar);
			builder.append(priceData.getClose());
			builder.append(delimitChar);
			builder.append(priceData.getHigh());
			builder.append(delimitChar);
			builder.append(priceData.getLow());
			builder.append(delimitChar);
			builder.append(priceData.getBidPrice());
			builder.append(delimitChar);
			builder.append(priceData.getAskPrice());
			builder.append(delimitChar);
		}
		
		return builder.toString();
	}
	
	
	private String getStringRepresentationOfPositionVariables(Boolean parameterDefinitions,String delimitChar){
		StringBuilder builder = new StringBuilder();
		
		builder.append(parameterDefinitions?"equity=":StringUtils.EMPTY);
		builder.append(equity);
		builder.append(delimitChar);
		builder.append(parameterDefinitions?"balance=":StringUtils.EMPTY);
		builder.append(account.getBalance());
		builder.append(delimitChar);
		builder.append(parameterDefinitions?"margin=":StringUtils.EMPTY);
		builder.append(margin);
		builder.append(delimitChar);
		builder.append(parameterDefinitions?"freeMargin=":StringUtils.EMPTY);
		builder.append(freeMargin);
		builder.append(delimitChar);
		builder.append(parameterDefinitions?"marginLevel=":StringUtils.EMPTY);
		builder.append(marginLevel);
		builder.append(delimitChar);
		builder.append(parameterDefinitions?"openPL=":StringUtils.EMPTY);
		builder.append(openPL);
		builder.append(delimitChar);
		
		
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(getStringRepresentationOfPositionVariables(true," "));
		
		builder.append("currentPrice=");
		builder.append(priceData);
		builder.append("\r\n");
		
		
		for(IPosition pos:positionList){
			builder.append("\t\tposition=");
			builder.append(pos.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}

	public PositionManager(List<IPosition> positionList, IAccount account, IPerformanceReportManager perfReporManager,MarketType marketType,String uuid) {
		super();
		this.positionList = positionList;
		this.account = account;
		this.perfReporManager = perfReporManager;
		this.marketType = marketType;
		this.uuid = uuid;
		
		initialBalance = this.account.getBalance();
		
		if(this.positionList == null){
			this.positionList = new ArrayList<IPosition>();
			this.tradeHistory = new ArrayList<Trade>();
			this.executionHistory = new ArrayList<ExecutionRecord>();
		}
	}
	

	public Double getInitialBalance() {
		return initialBalance;
	}

	public MarketType getMarketType() {
		return marketType;
	}

	public IPerformanceReportManager getPerfReporManager() {
		return perfReporManager;
	}

	private IPosition getPosition(Integer positionNo){
		IPosition result = null;
		if(positionNo == null){
			Integer posNo = this.positionList.size();
			result = new Position(posNo,TradeStatusType.CLOSE);
			result.updatePriceData(getPriceData());
			positionList.add(result);
		}else{
			result = positionList.get(positionNo);
		}
		
		return result;
			
	}
	
	private void update(){
		
		updateMargin();
		
		updateOpenPL();
		
		updateEquity();
		
		updateFreeMargin();
		
		updateMarginLevel();
		
	}


	private void updateMarginLevel() {
		marginLevel = TradeUtils.getRoundedUpValue((equity/(margin<=0?equity:margin))*100d);
	}

	public void updateGraphData(IStrategy strategy){
		if(graphLog){
			
			executionHistory.add(new ExecutionRecord(priceData, equity));
			
			if(!getIsHeadersWritten()){
				updateIndicatorData(strategy.getIndicatorHeaders());
				isHeadersWritten = true;
			}
			
			updateIndicatorData(strategy.getIndicatorData());
			updateTradeData();
		}
	}
	
	public void updateIndicatorData(String value){
		StringBuilder log = new StringBuilder();
		
		
		log.append(priceData.getTime().getTime());
		log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
		log.append(value);
		
		perfReporManager.getIndicatorLogger().writeLog(log.toString());
}

	
	private void updateTradeData(){
			StringBuilder log = new StringBuilder();
			
			log.append(priceData.getTime().getTime());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(priceData.getOpen());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(priceData.getHigh());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(priceData.getLow());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(priceData.getClose());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(priceData.getVolume());
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			log.append(equity.toString());
		
			perfReporManager.getGraphLogger().writeLog(log.toString());
	}

	

	@Override
	public Trade addTrade(Trade data) {
		
		Trade trade = getPosition(data.getPositionNo()).addTrade(data);
		
		if(trade.getStatus() == TradeStatusType.CLOSE){
			tradeHistory.add(trade);
			
			updateBalance(trade);
			
			update();
		}
		
		return trade;
	}




	private void updateBalance(Trade trade) {
		account.setBalance(TradeUtils.getRoundedUpValue(account.getBalance() + trade.getProfitLoss()));
	}

	private void updateMargin() {
		margin = TradeUtils.getRoundedUpValue(((getMarginLotCount()*getWeightedAverageEntryPrice()*getMarketType().getLotSize()) / getMarketType().getLeverage()));
	}

	
	
	@Override
	public List<IPosition> getPositions() {
		
		return positionList;
	}
	
	private void updateOpenPL(){
		openPL = 0d;
		
		for(IPosition pos:positionList){
			if(pos.getStatus() == TradeStatusType.OPEN)
				openPL += pos.getOpenPL();
		}
	}

	@Override
	public Double getOpenPL() {
		
		return openPL;
	}
	
	
	public Integer getTotalNumberOfOpenPositionCount(Date startDate, Date endDate){
		Integer result = 0;
		
		for (IPosition pos : positionList) {
		
			if (pos.getStatus() == TradeStatusType.OPEN && startDate==null && endDate==null) {
				result += 1;
				
			}else{
				if(pos.getStatus() == TradeStatusType.OPEN && startDate!=null && endDate==null && pos.getEntryDate().compareTo(startDate) >=0){
					result += 1;
					
				} else if(endDate!=null && startDate==null && (pos.getEntryDate().compareTo(endDate) < 0 && ( pos.getExitDate()==null || (pos.getExitDate().compareTo(endDate) > 0)) )){
                    result += 1;
                    
	             } else if(startDate!=null && endDate!=null &&  pos.getEntryDate().compareTo(startDate) >=0 && (pos.getEntryDate().compareTo(endDate) < 0) && ( pos.getExitDate()==null || (pos.getExitDate().compareTo(endDate) > 0) ) ){
	                    result += 1;
	             }
			}
		}
		
		return result;
	}
	
	public Double getStandardErrorOfWinningTrades(Date startDate, Date endDate){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (Trade tr : tradeHistory) {

			if(tr.getProfitLoss() > 0d){
				if (tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL) && startDate==null && endDate==null) {	
					stats.addValue(tr.getProfitLoss());
				}else{
					if(tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)){
						if(startDate!=null && endDate==null){
							if(tr.getRealizedDate().compareTo(startDate) >= 0){
								stats.addValue(tr.getProfitLoss());
							}
							
						} else if(endDate!=null && startDate==null){
							if(tr.getRealizedDate().compareTo(endDate) < 0){
								stats.addValue(tr.getProfitLoss());
							}
							
						} else if(startDate!=null && endDate!=null){
							if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
								stats.addValue(tr.getProfitLoss());
							}
						}
					}
					
				}
			}
		}

		return stats.getStandardDeviation() / Math.sqrt(stats.getN()==0?1:stats.getN());
	}
	
	public Integer getConsecutiveWinningTrades(Date startDate, Date endDate){
	
		Integer result = 0;
		Integer rally = 0;
		
		
		for (Trade tr : tradeHistory) {
		
			if (tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL) && startDate==null && endDate==null) {
				
				if(tr.getProfitLoss()>0) rally++;
				if(tr.getProfitLoss()<0) rally=0;
				if (rally>result) result = rally;
				
			}else{
				if(tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)){
					if(startDate!=null && endDate==null){
						if(tr.getRealizedDate().compareTo(startDate) >= 0){
							if(tr.getProfitLoss()>0) rally++;
							if(tr.getProfitLoss()<0) rally=0;
							if (rally>result) result = rally;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getRealizedDate().compareTo(endDate) < 0){
							if(tr.getProfitLoss()>0) rally++;
							if(tr.getProfitLoss()<0) rally=0;
							if (rally>result) result = rally;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							if(tr.getProfitLoss()>0) rally++;
							if(tr.getProfitLoss()<0) rally=0;
							if (rally>result) result = rally;
						}
					}
				}
				
			}
		}
		
		return result;
	}
	
	public Integer getConsecutiveLosingTrades(Date startDate, Date endDate){
		Integer result = 0;
		Integer rally = 0;
		
		
		for (Trade tr : tradeHistory) {
			
			if (tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL) && startDate==null && endDate==null) {
				
				if(tr.getProfitLoss()<0) rally++;
				if(tr.getProfitLoss()>0) rally=0;
				if (rally>result) result = rally;
				
			}else{
				if(tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)){
					if(startDate!=null && endDate==null){
						if(tr.getRealizedDate().compareTo(startDate) >= 0){
							if(tr.getProfitLoss()<0) rally++;
							if(tr.getProfitLoss()>0) rally=0;
							if (rally>result) result = rally;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getRealizedDate().compareTo(endDate) < 0){
							if(tr.getProfitLoss()<0) rally++;
							if(tr.getProfitLoss()>0) rally=0;
							if (rally>result) result = rally;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							if(tr.getProfitLoss()<0) rally++;
							if(tr.getProfitLoss()>0) rally=0;
							if (rally>result) result = rally;
						}
					}
				}
				
			}
		}
		
		
		
		return result;
	}

	@Override
	public Trade executeTrade(Trade trade) {
		
		addTrade(trade);
		
    	return trade;
	}

	@Override
	public Trade realizeTrade(Trade trade) {
		
		addTrade(trade);
		
		return trade;
	}

	@Override
	public Trade cancelTrade(Trade trade) {
		if(Constants.DEBUG_ENABLED)
			System.out.println("cancelTrade()");
		return addTrade(trade);
		
	}

	@Override
	public void generatePerformanceReport() {
		
		this.perfReporManager.generatePerformanceReport(this, getMarketType(),graphLog);
		
		if(graphLog){
			for(Trade trade:tradeHistory){
				StringBuilder builder = new StringBuilder();
				builder.append(trade.getRealizedDate().getTime());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getPositionNo());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getTradeNo());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getSignal().ordinal());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getLot());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getRealizedPrice());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getProfitLoss());
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
				builder.append(trade.getTradeType().ordinal());
	
				
				perfReporManager.getTradeLogger().writeLog(builder.toString());
			}
		
		perfReporManager.finalizeLogger();
		}
		
		
		
	}
	
}
