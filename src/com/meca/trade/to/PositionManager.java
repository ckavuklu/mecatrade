package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

import com.db4o.monitoring.internal.AveragingTimedReading;

public class PositionManager extends MecaObject implements IPositionManager{

	private List<IPosition> positionList;
	private List<Trade> tradeHistory;
	private PriceData priceData;
	private Double equity = 0d;
	private Double margin = 0d;
	private Double freeMargin = 0d;
	private Double marginLevel = 0d;
	private Double openPL = 0d;
	private IAccount account;
	private MarketType marketType;
	private Double marginLotCount = 0d;
	private Double weightedAverageEntryPrice = 0d;
	
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
		
		weightedAverageEntryPrice = result / totalLotCount;
		
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
			if(pos.getStatus() == TradeStatusType.OPEN)
				pos.updatePriceData(priceData);
		}
		
		
		updateOpenPL();
		
		updateEquity();
		
		updateFreeMargin();
		
		
	}

	private void updateFreeMargin() {
		freeMargin = getEquity() - getMargin();
	}

	private void updateEquity() {
		equity = account.getBalance() + getOpenPL();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("equity=");
		builder.append(equity);
		builder.append(" ");
		builder.append("margin=");
		builder.append(margin);
		builder.append(" ");
		builder.append("freeMargin=");
		builder.append(freeMargin);
		builder.append(" ");
		builder.append("marginLevel=");
		builder.append(marginLevel);
		builder.append(" ");
		builder.append("openPL=");
		builder.append(openPL);
		builder.append(" ");
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

	public PositionManager(List<IPosition> positionList, IAccount account, IPerformanceReportManager perfReporManager,MarketType marketType) {
		super();
		this.positionList = positionList;
		this.account = account;
		this.perfReporManager = perfReporManager;
		this.marketType = marketType;
		
		if(this.positionList == null){
			this.positionList = new ArrayList<IPosition>();
			this.tradeHistory = new ArrayList<Trade>();
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
			positionList.add(result);
		}else{
			result = positionList.get(positionNo);
		}
		
		return result;
			
	}
	
	

	@Override
	public Trade addTrade(Trade data) {
		
		Trade trade = getPosition(data.getPositionNo()).addTrade(data);
		
		if(trade.getStatus() == TradeStatusType.CLOSE){
			tradeHistory.add(trade);
		}
		
		
		updateMargin();

		return trade;
	}

	private void updateMargin() {
		margin = ((getMarginLotCount()*getWeightedAverageEntryPrice()) / getMarketType().getLeverage());
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
		
		switch(trade.getTradeType()){
	    	case BUY : {
	    		
	    		withdraw(addTrade(trade));
	    		
	    		break;
	    	}
	
	    	case LEXIT : {
	    		
	    		addTrade(trade);
	
	    		break;
	    	}
	
	    	
	    	case SELL : {
	    		
	    		withdraw(addTrade(trade));
	    		
	    		break;
	    	}
	
	    	case SEXIT : {
	    		
	    		addTrade(trade);
	    		
	    		break;
	    	}
	
	    	default:{
	    		break;
	    	}
		}
    	
    	return trade;
	}

	@Override
	public Trade realizeTrade(Trade trade) {
		switch(trade.getTradeType()){
    	case BUY : {
    		
    		withdraw(addTrade(trade));

    		break;
    	}
    	
    	case LEXIT : {
    		
    		deposit(addTrade(trade));

    		break;
    	}

    	case SELL : {
    		
    		withdraw(addTrade(trade));
    		
    		break;
    	}

    	case SEXIT : {
    		
    		deposit(addTrade(trade));
    		
    		break;
    	}

    	default:{
    		break;
    	}
	}
	

	
	return trade;
	}

	@Override
	public Trade cancelTrade(Trade trade) {
		
    	System.out.println("cancelTrade()");
    	cancel(trade);
		return addTrade(trade);
		
	}

	@Override
	public void generatePerformanceReport() {
		this.perfReporManager.generatePerformanceReport(this, getMarketType());
		
	}
	
	
	
	
	
	
	/**
	 * TODO: We assumed that only one account exist with the same currency type in the same account manager
	 * 
	 * 
	 * 
	 */
	@Override
	public Double getBalance(CurrencyType currency) {
		Double balance = 0d;

		if (account.getCurrency() == currency && account.isTradable()) {
			balance += account.getBalance();
		}
		
		
		return balance;
	}

	@Override
	public boolean withdraw(Trade trade) {
		boolean result=false;
		CurrencyType currency=null;
		Double blockAmount = null;
		
		
		if(trade.getTradeType() == TradeType.BUY){
			 currency = trade.getMarketType().getQuoteCurrency();
			 blockAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getEntryPrice();
			 
		}
		
		else if(trade.getTradeType() == TradeType.SELL){
			 currency = trade.getMarketType().getBaseCurrency();
			 blockAmount = trade.getLot() * trade.getMarketType().getLotSize();
		}
			
		
	
		
		if(account!=null){
			if (trade.getStatus() == TradeStatusType.CLOSE){
			
				Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * (trade.getTradeType() == TradeType.BUY?trade.getRealizedPrice():1d);
				result = account.withdrawRealized(blockAmount, realAmount);
	
			}
		}
		
		
		return result;
	}

	@Override
	public boolean deposit(Trade trade) {
		
		boolean result=false;
	
		
		if(account!=null && account!=null){
			
			if (trade.getStatus() == TradeStatusType.CLOSE){
				
				
				
				if(trade.getTradeType() == TradeType.SEXIT){
				
					Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * 1d;
					result = account.deposit(realAmount);
					account.deposit(trade.getProfitLoss());
				}else{
					Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getRealizedPrice();
					result = account.deposit(realAmount);
					//account.deposit(trade.getProfitLoss());
				}
	
			}
		}
		
		
		return result;
	}

	
	
	@Override
	public boolean cancel(Trade trade) {
		
		boolean result=false;
		CurrencyType currency=null;
		
		
		if(trade.getTradeType() == TradeType.BUY)
			 currency = trade.getMarketType().getQuoteCurrency();
		
		else if(trade.getTradeType() == TradeType.SELL)
			 currency = trade.getMarketType().getBaseCurrency();
			
		Double blockAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getEntryPrice();
		
		
		return false;
	}

	
	
}
