package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class BaseTrader implements ITrader {
	
	private IPositionManager positionManager;
	private Double askPrice;
	private Double bidPrice;
	

	public BaseTrader(IPositionManager positionManager) {

		this.positionManager = positionManager;
	}

	@Override
	public List<Trade> evaluateStrategyDecisions(
			List<StrategyDecision> decisionList) {
		
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();
		Double baseBalance = positionManager.getBalance(positionManager.getMarketType().getBaseCurrency());
		Double quoteBalance = positionManager.getBalance(positionManager.getMarketType().getQuoteCurrency());
		

		for(StrategyDecision decision : decisionList){

			
			if(decision.getDecision() == DecisionType.LONG){
	
				if(quoteBalance > 0){
					
					double lots = (quoteBalance / askPrice) / positionManager.getMarketType().getLotSize();
					
					Trade tradeData = new Trade();
					tradeData.setDate(new Date());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.BUY);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lots);
					tradeData.setEntryPrice(askPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					tradeList.add(tradeData);
					
				}
				
				for(IPosition p:positions){
					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.SELL){
						Trade tradeData = new Trade();
						tradeData.setDate(new Date());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.SEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setExitPrice(askPrice);
						tradeData.setMarketType(positionManager.getMarketType());
						tradeList.add(tradeData);
					}
				}
				
			}else if(decision.getDecision() == DecisionType.SHORT){
				
				if(baseBalance > 0){
					
					double lots = (baseBalance) / positionManager.getMarketType().getLotSize();

					Trade tradeData = new Trade();
					tradeData.setDate(new Date());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.SELL);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lots);
					tradeData.setEntryPrice(bidPrice);
					tradeData.setMarketType(positionManager.getMarketType());
					tradeList.add(tradeData);
				}
				
				for(IPosition p:positions){

					if(p.getStatus() == TradeStatusType.OPEN && p.getTradeType()==TradeType.BUY){
						Trade tradeData = new Trade();
						tradeData.setDate(new Date());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.LEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setExitPrice(bidPrice);
						tradeData.setMarketType(positionManager.getMarketType());
						tradeList.add(tradeData);
					}
					
				}

			}else {
				
			}
		}

		return tradeList;
	}

	@Override
	public List<Trade> endOfMarket() {
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();

		for(IPosition p:positions){

			if(p.getStatus() == TradeStatusType.OPEN){
				Trade tradeData = new Trade();
				tradeData.setDate(new Date());
				tradeData.setStatus(TradeStatusType.OPEN);
				tradeData.setPositionNo(p.getPositionNo());
				tradeData.setStatus(TradeStatusType.OPEN);
				tradeData.setTradeType(p.getTradeType()==TradeType.SELL?TradeType.SEXIT:TradeType.LEXIT);
				tradeData.setSignal(SignalType.Ex);
				tradeData.setLot(p.getOpenLotCount());
				tradeData.setExitPrice(p.getTradeType()==TradeType.SELL?askPrice:bidPrice);
				tradeData.setMarketType(positionManager.getMarketType());
				tradeList.add(tradeData);
			}
		}
		
		return tradeList;
	}

	@Override
	public void updatePriceData(PriceData data) {
		this.askPrice = data.getAskPrice();
		this.bidPrice = data.getBidPrice();
	}

}
