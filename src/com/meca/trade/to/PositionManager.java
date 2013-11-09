package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class PositionManager extends MecaObject implements IPositionManager{

	private List<IPosition> positionList;
	private List<Trade> tradeHistory;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(IPosition pos:positionList){
			builder.append("\t\tposition=");
			builder.append(pos.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}

	public PositionManager(List<IPosition> positionList) {
		super();
		this.positionList = positionList;
		
		if(this.positionList == null){
			this.positionList = new ArrayList<IPosition>();
			this.tradeHistory = new ArrayList<Trade>();
		}
	}
	
	private IPosition getPosition(Integer positionNo){
		IPosition result = null;
		if(positionNo == null){
			Integer posNo = this.positionList.size();
			result = new Position(posNo,TradeStatusType.OPEN);
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

		return trade;
	}

	
	
	@Override
	public List<IPosition> getPositions() {
		
		return positionList;
	}

	@Override
	public Double getTotalRisk() {
		return null;
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
	
}
