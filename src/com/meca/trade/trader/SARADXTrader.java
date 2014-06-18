package com.meca.trade.trader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.meca.trade.networks.Parameter;
import com.meca.trade.to.Constants;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.IPosition;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.IPositionSizer;
import com.meca.trade.to.ITrader;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.SignalType;
import com.meca.trade.to.StrategyDecision;
import com.meca.trade.to.Trade;
import com.meca.trade.to.TradeStatusType;
import com.meca.trade.to.TradeType;
import com.meca.trade.to.TradeUtils;
import com.meca.trade.to.VolatilityAdjustedPositionSizer;

public class SARADXTrader extends BaseTrader {
	
	protected Integer maxPositionCount = 5;
	
	public SARADXTrader(IPositionManager positionManager) {
		super(positionManager);
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	public List<Trade> evaluateStrategyDecisions(
			List<StrategyDecision> decisionList) {
		
		
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();
		Double freeMargin = positionManager.getFreeMargin();
		
		if(positionStopLossEnabled)
			evaluateRisk(tradeList);
		
		if(positionTakeProfitEnabled)
			evaluateProfit(tradeList); 
		
		
		for(StrategyDecision decision : decisionList){

			for(DecisionType decisionType: decision.getDecisionList()){
			
				if(decisionType == DecisionType.LONG_ENTRY){
				
				Double lotSize = positionSizer.getLotSize(TradeType.BUY);
	
				if(lotSize>0d && positionManager.getOpenPositionCount(TradeType.BUY) < maxPositionCount){
					
					Trade tradeData = new Trade();
					tradeData.setEntryDate(priceData.getTime());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.BUY);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lotSize);
					tradeData.setEntryPrice(askPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					
					if(positionStopLossEnabled)
						tradeData.setStopLoss(calculatePositionStopLoss(tradeData));
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					
					addToTradeList(tradeList,tradeData);
					
				}
				
				
				
			}else if(decisionType == DecisionType.SHORT_ENTRY){
				
				Double lotSize = positionSizer.getLotSize(TradeType.SELL);
				
				if(lotSize>0d && positionManager.getOpenPositionCount(TradeType.SELL) < maxPositionCount ){

					Trade tradeData = new Trade();
					tradeData.setEntryDate(priceData.getTime());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.SELL);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lotSize);
					tradeData.setEntryPrice(bidPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					
					if(positionStopLossEnabled)
						tradeData.setStopLoss(calculatePositionStopLoss(tradeData));
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					addToTradeList(tradeList,tradeData);
				}
				
				

			}else if(decisionType == DecisionType.SHORT_EXIT){
				for(IPosition p:positions){
					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.SELL){
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

					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.BUY){
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
