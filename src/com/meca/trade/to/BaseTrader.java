package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.meca.trade.networks.Parameter;

public class BaseTrader implements ITrader {
	
	protected IPositionManager positionManager;
	protected PriceData priceData;
	
	public IPositionManager getPositionManager() {
		return positionManager;
	}


	public Double getAskPrice() {
		return askPrice;
	}


	public Double getBidPrice() {
		return bidPrice;
	}

	protected Double askPrice;
	protected Double bidPrice;
	
	protected Boolean positionStopLossEnabled = Boolean.FALSE;
	protected Boolean positionTakeProfitEnabled = Boolean.FALSE;
	
	
	protected String positionStopLossType;
	protected Double positionStopLossValue;

	protected Double strategyStopLossPercentage = null;

	
	public Double getStrategyStopLossPercentage() {
		return strategyStopLossPercentage;
	}


	public void setStrategyStopLossPercentage(Double strategyStopLossPercentage) {
		this.strategyStopLossPercentage = strategyStopLossPercentage;
	}

	private String positionTakeProfitType;
	private Double positionTakeProfitValue;
	
	private IPositionSizer positionSizer;
	
	public BaseTrader(IPositionManager positionManager) {

		this.positionManager = positionManager;
		this.positionSizer = new VolatilityAdjustedPositionSizer(Constants.DEFAULT_POSITION_SIZE_PERCENTAGE,this);
	}
	
	
	protected Double calculatePositionStopLoss(Trade data){
		Double result = null;
		
		if(data.getTradeType()==TradeType.SELL || data.getTradeType()==TradeType.BUY){
			if(positionStopLossEnabled){
				if(positionStopLossType.equalsIgnoreCase(Constants.VALUE_TYPE_EQUITY_PERCENTAGE)){
					Double difference = ((positionManager.getEquity()*positionStopLossValue/100d) / (positionManager.getMarketType().getLotSize()*data.getLot()));
					difference = ((data.getTradeType()==TradeType.SELL)?1d:-1d) * difference;
					result = data.getEntryPrice() +  difference ;
					
				}else if(positionStopLossType.equalsIgnoreCase(Constants.VALUE_TYPE_POSITION_PERCENTAGE)){
					
					Double difference = ((data.getEntryPrice()*positionStopLossValue/100d) / positionManager.getMarketType().getLeverage());
					difference = ((data.getTradeType()==TradeType.SELL)?1d:-1d) * difference;
					result = data.getEntryPrice() +  difference;
					
				}else if(positionStopLossType.equalsIgnoreCase(Constants.VALUE_TYPE_POINT_TYPE)){
					Double difference = ((data.getTradeType()==TradeType.SELL)?1d:-1d) * positionStopLossValue;
					result = data.getEntryPrice() +  difference;
				}
				
			}
		}
		
		return TradeUtils.roundUpDigits(result, positionManager.getMarketType().getPricePrecision());
	}
	
	
	protected Double calculatePositionTakeProfit(Trade data){
		Double result = null;
		
		if(data.getTradeType()==TradeType.SELL || data.getTradeType()==TradeType.BUY){
			if(positionTakeProfitEnabled){
				if(positionTakeProfitType.equalsIgnoreCase(Constants.VALUE_TYPE_EQUITY_PERCENTAGE)){
					Double difference = ((positionManager.getEquity()*positionTakeProfitValue/100d) / (positionManager.getMarketType().getLotSize()*data.getLot()));
					difference = ((data.getTradeType()==TradeType.SELL)?-1d:1d) * difference;
					result = data.getEntryPrice() +  difference ;
					
				}else if(positionTakeProfitType.equalsIgnoreCase(Constants.VALUE_TYPE_POSITION_PERCENTAGE)){
					
					Double difference = ((data.getEntryPrice()*positionTakeProfitValue/100d) / positionManager.getMarketType().getLeverage());
					difference = ((data.getTradeType()==TradeType.SELL)?-1d:1d) * difference;
					result = data.getEntryPrice() +  difference;
					
				}else if(positionTakeProfitType.equalsIgnoreCase(Constants.VALUE_TYPE_POINT_TYPE)){
					Double difference = ((data.getTradeType()==TradeType.SELL)?-1d:1d) * positionTakeProfitValue;
					result = data.getEntryPrice() +  difference;
				}
				
			}
		}
		
		return TradeUtils.roundUpDigits(result, positionManager.getMarketType().getPricePrecision());
	}
	
	
	protected void evaluateRisk(List<Trade> tradeList){
		List<IPosition> positions = positionManager.getPositions();
		
		for(IPosition p:positions){
			if(p.getStatus() == TradeStatusType.OPEN){
				
				if(p.getTradeType() == TradeType.SELL){
					
					if(askPrice >= p.getStopLoss()){
						
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.SEXIT);
						tradeData.setSignal(SignalType.Rs);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setEntryPrice(p.getEntryPrice());
						tradeData.setExitPrice(askPrice);
						tradeData.setMarketType(positionManager.getMarketType());
						addToTradeList(tradeList,tradeData);
						
					}
				}else if(p.getTradeType() == TradeType.BUY){
					if(bidPrice <= p.getStopLoss()){
						
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.LEXIT);
						tradeData.setSignal(SignalType.Rs);
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
	
	
	protected void evaluateProfit(List<Trade> tradeList){
		List<IPosition> positions = positionManager.getPositions();
		
		for(IPosition p:positions){
			if(p.getStatus() == TradeStatusType.OPEN){
				
				if(p.getTradeType() == TradeType.SELL){
					
					if(askPrice <= p.getTakeProfit()){
						
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.SEXIT);
						tradeData.setSignal(SignalType.Pt);
						tradeData.setLot(p.getOpenLotCount());
						tradeData.setEntryPrice(p.getEntryPrice());
						tradeData.setExitPrice(askPrice);
						tradeData.setMarketType(positionManager.getMarketType());
						addToTradeList(tradeList,tradeData);
						
					}
				}else if(p.getTradeType() == TradeType.BUY){
					if(bidPrice >= p.getTakeProfit()){
						
						Trade tradeData = new Trade();
						tradeData.setEntryDate(priceData.getTime());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setPositionNo(p.getPositionNo());
						tradeData.setStatus(TradeStatusType.OPEN);
						tradeData.setTradeType(TradeType.LEXIT);
						tradeData.setSignal(SignalType.Pt);
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
	
	
	private void addToTradeList(List<Trade> tradeList, Trade tradeData){
		if(!isTradeShouldBeFiltered(tradeList, tradeData)){
			tradeList.add(tradeData);
		}
	}
	
	/*
	private boolean contains(List<Trade> tradeList, Trade tradeData){
		boolean result = false;
		
		for(Trade data:tradeList){
		 result =  (data.getPositionNo() == tradeData.getPositionNo()) && data.getLot()==tradeData.getLot() && data.getTradeType()==tradeData.getTradeType() && data.getStatus()==tradeData.getStatus();
		 
		 if(result)
			 break;
		}
		
		return result;
	}*/
	
	private Boolean isTradeShouldBeFiltered(List<Trade> tradeList, Trade tradeData){
		Boolean result = Boolean.FALSE;
		
		if(tradeData.getLot() <= 0.00d){
			result = Boolean.TRUE;		
		}
		
		if(tradeList.contains(tradeData)){
			result = Boolean.TRUE;
		}
		
		if(result)
			System.out.println("FILTERED: " + tradeData);
		
		return result;
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

			if(decision.getDecision() == DecisionType.LONG){
				
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
					
					if(positionStopLossEnabled)
						tradeData.setStopLoss(calculatePositionStopLoss(tradeData));
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					
					addToTradeList(tradeList,tradeData);
					
				}
				
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
				
			}else if(decision.getDecision() == DecisionType.SHORT){
				
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
					
					if(positionStopLossEnabled)
						tradeData.setStopLoss(calculatePositionStopLoss(tradeData));
					
					if(positionTakeProfitEnabled)
						tradeData.setTakeProfit(calculatePositionTakeProfit(tradeData));
					
					addToTradeList(tradeList,tradeData);
				}
				
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

			}else {
				
			}
		}

		return tradeList;
	}

	@Override
	public void setConfiguration(List<Parameter> paramList) {
		
		for(Parameter param:paramList){
			if(param.getName().equalsIgnoreCase(Constants.TYPE_POSITION_STOP_LOSS)){
				positionStopLossEnabled = Boolean.TRUE;
				positionStopLossType = param.getType();
				positionStopLossValue = (Double)param.getValue();
				
			}else if(param.getName().equalsIgnoreCase(Constants.TYPE_POSITION_TAKE_PROFIT)){
				positionTakeProfitEnabled = Boolean.TRUE;
				positionTakeProfitType = param.getType();
				positionTakeProfitValue = (Double)param.getValue();
			}else if(param.getName().equalsIgnoreCase(Constants.TYPE_POSITION_SIZER)){
				if(param.getType().equalsIgnoreCase(Constants.VALUE_TYPE_POSITION_SIZER_VOLATILITY_ADJUSTED))
					positionSizer = new VolatilityAdjustedPositionSizer((Double)param.getValue(),this);
				
			}else if(param.getName().equalsIgnoreCase(Constants.TYPE_STRATEGY_STOP_LOSS)){

				if(param.getType().equalsIgnoreCase(Constants.VALUE_TYPE_STRATEGY_STOP_LOSS))
					strategyStopLossPercentage = (Double)param.getValue();
			}
		}
		
		
		//PARSE
	}


	@Override
	public List<Trade> endOfMarket(SignalType signalType) {
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		
		List<IPosition> positions = positionManager.getPositions();

		
		for(IPosition p:positions){

			if(p.getStatus() == TradeStatusType.OPEN){
				Trade tradeData = new Trade();
				tradeData.setEntryDate(priceData.getTime());
				tradeData.setStatus(TradeStatusType.OPEN);
				tradeData.setPositionNo(p.getPositionNo());
				tradeData.setStatus(TradeStatusType.OPEN);
				tradeData.setTradeType(p.getTradeType()==TradeType.SELL?TradeType.SEXIT:TradeType.LEXIT);
				tradeData.setSignal(signalType);
				tradeData.setLot(p.getOpenLotCount());
				tradeData.setEntryPrice(p.getEntryPrice());
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
		this.priceData = data;
	}

}
