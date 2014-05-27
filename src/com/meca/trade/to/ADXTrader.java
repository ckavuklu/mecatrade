package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class ADXTrader extends BaseTrader {

	public ADXTrader(IPositionManager positionManager) {
		super(positionManager);
		// TODO Auto-generated constructor stub
		
	}

	
	public List<Trade> evaluateStrategyDecisions(
			List<StrategyDecision> decisionList) {
		
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();
		Double freeMargin = positionManager.getFreeMargin();
		
//		if(positionStopLossEnabled)
			evaluateRisk(tradeList);
		
		if(positionTakeProfitEnabled)
			evaluateProfit(tradeList);
		
		for(StrategyDecision decision : decisionList){

			for(DecisionType decisionType: decision.getDecisionList()){
			
				if(decisionType == DecisionType.LONG_ENTRY){
				
				Double lotSize = positionSizer.getLotSize(TradeType.BUY);
	
				if(lotSize>0d){
					
					Trade tradeData = new Trade();
					tradeData.setEntryDate(priceData.getTime());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.BUY);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lotSize);
					tradeData.setEntryPrice(askPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					
					tradeData.setStopLoss(priceData.getLow());
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					addToTradeList(tradeList,tradeData);
					
				}
				
				
				
			}else if(decisionType == DecisionType.SHORT_ENTRY){
				
				Double lotSize = positionSizer.getLotSize(TradeType.SELL);
				
				if(lotSize>0d){

					Trade tradeData = new Trade();
					tradeData.setEntryDate(priceData.getTime());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.SELL);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lotSize);
					tradeData.setEntryPrice(bidPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					
					tradeData.setStopLoss(priceData.getHigh());
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					addToTradeList(tradeList,tradeData);
				}
				
				

			}else if(decisionType == DecisionType.SHORT_EXIT){
				for(IPosition p:positions){
					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.SELL && (p.getStopLoss() < askPrice || p.getEntryPrice() > askPrice)){
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.SEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setEntryPrice(p.getEntryPrice());
						tradeData.setExitPrice(askPrice);
						tradeData.setMarketType(positionManager.getMarketType());
						addToTradeList(tradeList,tradeData);
					}
				}
			}else if(decisionType == DecisionType.LONG_EXIT){
				for(IPosition p:positions){

					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.BUY && (p.getStopLoss() > bidPrice || p.getEntryPrice() < bidPrice)){
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.LEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setExitPrice(bidPrice);
						tradeData.setEntryPrice(p.getEntryPrice());
						tradeData.setMarketType(positionManager.getMarketType());
						addToTradeList(tradeList,tradeData);
					}
					
				}
			}
		}
		}

		return tradeList;
	}
	
	
}
