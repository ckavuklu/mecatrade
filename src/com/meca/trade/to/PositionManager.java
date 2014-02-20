package com.meca.trade.to;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.db4o.monitoring.internal.AveragingTimedReading;

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
	private String uuid;
	private List<ExecutionRecord> executionHistory = null; 
	
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
		
		if(this.positionList == null){
			this.positionList = new ArrayList<IPosition>();
			this.tradeHistory = new ArrayList<Trade>();
			this.executionHistory = new ArrayList<ExecutionRecord>();
		}
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

	public void updateGraphData(boolean endOfMarket){
		if(graphLog){
			StringBuilder log = new StringBuilder();
			
			log.append(Constants.GRAPH_DATA_JSON_START_STRING);
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
			log.append(Constants.GRAPH_DATA_JSON_END_STRING);
			
			executionHistory.add(new ExecutionRecord(priceData, equity));
			
			log.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			
			perfReporManager.getGraphLogger().writeLog(log.toString());
		}
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
	
	
	public Integer getConsecutiveWinningTrades(){
	
		Integer result = 0;
		Integer rally = 0;
		
		for (Trade tr : tradeHistory) {
		
			if (tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)) {
				
				if(tr.getProfitLoss()>0) rally++;
				if(tr.getProfitLoss()<0) rally=0;
				if (rally>result) result = rally;
				
			}
		}
		
		return result;
	}
	
	public Integer getConsecutiveLosingTrades(){
	
		Integer result = 0;
		Integer rally = 0;
		
		for (Trade tr : tradeHistory) {
		
			if (tr.getStatus() == TradeStatusType.CLOSE && !(tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)) {
				
				if(tr.getProfitLoss()<0) rally++;
				if(tr.getProfitLoss()>0) rally=0;
				if (rally>result) result = rally;
				
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
			perfReporManager.finalizeLogger();
		}
		
	}
	
}
