package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;


public class Position extends MecaObject implements IPosition {

	public TradeStatusType getStatus() {
		return status;
	}

	private Integer positionNo;
	private TradeStatusType status;
	private Double openLotCount;
	private List<Trade> tradeList;
	
	public Integer getPositionNo() {
		return positionNo;
	}

	public void setPositionNo(Integer positionNo) {
		this.positionNo = positionNo;
	}

	private MarketType marketType;
	private TradeType tradeType;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("positionNo=");
		builder.append(positionNo);
		builder.append(" ");
		builder.append("status=");
		builder.append(status);
		builder.append(" ");
		builder.append("openLotCount=");
		builder.append(openLotCount);
		builder.append("\r\nTrades\r\n");
		
		for(Trade tr : tradeList){
			builder.append("\t\ttrade=");
			builder.append(tr.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}
	
	public Position(Integer positionNo, TradeStatusType status,
			List<Trade> tradeList) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = tradeList;
		this.openLotCount = 0d;
	}
	
	public Position(Integer positionNo, TradeStatusType status) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = new ArrayList<Trade>();
		this.openLotCount = 0d;
	}

	public Double getOpenLotCount() {
		return openLotCount;
	}

	@Override
	public Trade addTrade(Trade trade) {
		if(trade.getPositionNo()==null){
			trade.setPositionNo(positionNo);
		}
		
		if(trade.getTradeNo()==null){
			trade.setTradeNo(tradeList.size());
			tradeList.add(trade);
		}else{
			tradeList.set(trade.getTradeNo(), trade);
		}
		
		if (trade.getTradeType() == TradeType.BUY || trade.getTradeType() == TradeType.SELL) {
			marketType = trade.getMarketType();
			tradeType = trade.getTradeType();
			
			if(trade.getStatus()== TradeStatusType.CLOSE)
				openLotCount = trade.getLot();
			else if(trade.getStatus()== TradeStatusType.CANCEL){
				if (tradeList.size() == 1) { 
					this.status = TradeStatusType.CANCEL;
				}
			}
		}
		else if ((trade.getTradeType() == TradeType.LEXIT || trade.getTradeType() == TradeType.SEXIT)){
			
			switch (trade.getStatus()) {
				case OPEN: {
					openLotCount -= trade.getLot();
	
					if (openLotCount == 0d)
						status = TradeStatusType.CLOSE;
					break;
				}
				case CANCEL: {
					openLotCount += trade.getLot();
					break;
				}
				case CLOSE: {
					trade.setProfitLoss(evaluateTradeProfitLossAccordingToQuote(trade));
					break;
				}
				default: {
					break;
				}
			}
	
		}

		
		return trade;
	}

	private Double evaluateTradeProfitLossAccordingToQuote(Trade trade) {
		Double result = 0d;
		Double entryPrice = getEntryPrice();
		
		result = ((trade.getTradeType() == TradeType.LEXIT) ?  (trade.getRealizedPrice() - entryPrice) : (entryPrice - trade.getRealizedPrice()))
				* trade.getLot() 
				* trade.getMarketType().getLotSize();
	
		return result;
	}
	

	
	public Double getCurrentRisk() { 
		return null;
	}


	private Double getEntryPrice(){
		Double result = 0d;
		for (Trade tr : tradeList) {
			if ((tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)
					&& (tr.getStatus() == TradeStatusType.CLOSE)) {
						result = tr.getRealizedPrice();
					break;
				}
		}
		
		return result;
	}
	
	
	public TradeType getEntryTradeType(){
		TradeType result = null;
		for (Trade tr : tradeList) {
			if ((tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)
					&& (tr.getStatus() == TradeStatusType.CLOSE)) {
						result = tr.getTradeType();
					break;
				}
		}
		
		return result;
	}
	

	@Override
	public Double getCurrentProfitLoss(Double quote) {
		Double result = 0d;
		Double entryPrice = getEntryPrice();
		
		result = openLotCount * marketType.getLotSize() * ((tradeType == TradeType.BUY) ?  (quote - entryPrice) : (entryPrice - quote));
				
		return result;
	}

	@Override
	public Double getRealizedProfitLoss() {
		Double result = 0d;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				
				result += tr.getProfitLoss();
			}
		}
		
		return result;
	}

	@Override
	public Double getRealizedGrossProfit() {
		Double result = 0d;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				
				if(tr.getProfitLoss() > 0)
					result += tr.getProfitLoss();
			}
		}
		
		return result;
	}
	
	@Override
	public Integer getTotalNumberOfEntryTrades() {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && (tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)) {
				result += 1;
			}
		}
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfTrades() {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				result += 1;
			}
		}
		
		return result;
	}
	
	public Double getLargestWinningTrade(){
		Double result = 0d;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				
				if(result < tr.getProfitLoss())
					result = tr.getProfitLoss();
			}
		}
		
		return result;
		
	}
	
	public Double getLargestLosingTrade(){
		Double result = 0d;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				
				if(result > tr.getProfitLoss())
					result = tr.getProfitLoss();
			}
		}
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfWinningTrades() {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				if(tr.getProfitLoss() > 0)
					result += 1;
			}
		}
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfLosingTrades() {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				if(tr.getProfitLoss() < 0)
					result += 1;
			}
		}
		
		return result;
	}

	@Override
	public Double getRealizedGrossLoss() {
		Double result = 0d;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE) {
				
				if(tr.getProfitLoss() < 0)
					result += tr.getProfitLoss();
			}
		}
		
		return result;
	}
	
}
