package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;


public class Position extends MecaObject implements IPosition {

	private Integer positionNo;
	private TradeStatusType status;
	private List<Trade> tradeList;
	
	
	public Position(Integer positionNo, TradeStatusType status,
			List<Trade> tradeList) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = tradeList;
	}
	
	public Position(Integer positionNo, TradeStatusType status) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = new ArrayList<Trade>();
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
		
		return trade;
	}

	

	public Double getCurrentRisk() { return null;}
	
	public Double getCurrentProfitLoss() { return null;}
	
}
