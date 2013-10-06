package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;


public class Position extends MecaObject implements IPosition {

	private Integer positionNo;
	private TradeStatusType status;
	private Double openLotCount;
	private List<Trade> tradeList;
	
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

	private Double getOpenLotCount() {
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
			if(trade.getStatus()== TradeStatusType.CLOSE)
				openLotCount = trade.getLot();
			else if(trade.getStatus()== TradeStatusType.CANCEL){
				if (tradeList.size() == 1) { 
					this.status = TradeStatusType.CANCEL;
				}
			}
		}
		else if ((trade.getTradeType() == TradeType.LEXIT || trade.getTradeType() == TradeType.SEXIT)){
			if(trade.getStatus()== TradeStatusType.OPEN){
				openLotCount -= trade.getLot();
				
				if(openLotCount==0d) 
					status = TradeStatusType.CLOSE;
				
			}else if(trade.getStatus()== TradeStatusType.CANCEL){
				openLotCount += trade.getLot();
			}
			
			
		}
		
		
		
		return trade;
	}

	

	public Double getCurrentRisk() { return null;}
	
	public Double getCurrentProfitLoss() { return null;}
	
}
