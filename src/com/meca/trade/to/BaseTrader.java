package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class BaseTrader implements ITrader {
	
	private IPositionManager positionManager;
	private IAccountManager accountManager;
	private IMarketManager marketManager;
	private Double price;
	

	public BaseTrader(IMarketManager marketManager) {

		this.positionManager = marketManager.getPositionManager();
		this.accountManager = marketManager.getAccountManager();
		this.marketManager = marketManager;
	}

	@Override
	public List<Trade> evaluateStrategyDecisions(
			List<StrategyDecision> decisionList) {
		
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();
		Double baseBalance = accountManager.getBalance(marketManager.getMarketType().getBaseCurrency());
		Double quoteBalance = accountManager.getBalance(marketManager.getMarketType().getQuoteCurrency());
		

		for(StrategyDecision decision : decisionList){

			price = decision.getPrice().getClose();
			
			
			if(decision.getDecision() == DecisionType.LONG){
				
				
				if(quoteBalance > 0){
					
					double lots = (quoteBalance / price) / marketManager.getMarketType().getLotSize();
					
					Trade tradeData = new Trade();
					tradeData.setDate(new Date());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.BUY);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lots);
					tradeData.setOpenPrice(price);
					tradeData.setMarketType(marketManager.getMarketType());
					tradeList.add(tradeData);
					
				}
				
				for(IPosition p:positions){
					if(p.getStatus() == TradeStatusType.OPEN && p.getEntryTradeType()==TradeType.SELL){
						Trade tradeData = new Trade();
						tradeData.setDate(new Date());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.SEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setOpenPrice(price);
						tradeData.setMarketType(marketManager.getMarketType());
						tradeList.add(tradeData);
					}
				}
				
			}else if(decision.getDecision() == DecisionType.SHORT){
				
				if(baseBalance > 0){
					
					double lots = (baseBalance) / marketManager.getMarketType().getLotSize();

					Trade tradeData = new Trade();
					tradeData.setDate(new Date());
					tradeData.setStatus(TradeStatusType.OPEN);
					tradeData.setTradeType(TradeType.SELL);
					tradeData.setSignal(SignalType.En);
					tradeData.setLot(lots);
					tradeData.setOpenPrice(price);
					tradeData.setMarketType(marketManager.getMarketType());
					tradeList.add(tradeData);
				}
				
				for(IPosition p:positions){

					if(p.getStatus() == TradeStatusType.OPEN && p.getEntryTradeType()==TradeType.BUY){
						Trade tradeData = new Trade();
						tradeData.setDate(new Date());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.LEXIT);
						tradeData.setSignal(SignalType.Ex);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setOpenPrice(price);
						tradeData.setMarketType(marketManager.getMarketType());
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
				tradeData.setTradeType(p.getEntryTradeType()==TradeType.SELL?TradeType.SEXIT:TradeType.LEXIT);
				tradeData.setSignal(SignalType.Ex);
				tradeData.setLot(p.getOpenLotCount());
				tradeData.setOpenPrice(price);
				tradeData.setMarketType(marketManager.getMarketType());
				tradeList.add(tradeData);
			}
		}
		
		return tradeList;
	}

}
