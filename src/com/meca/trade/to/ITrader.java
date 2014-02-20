package com.meca.trade.to;

import java.io.Serializable;
import java.util.List;

import com.meca.trade.networks.Parameter;

public interface ITrader extends Serializable, IPriceData {
	List<Trade> evaluateStrategyDecisions(List<StrategyDecision> decisionList);
	
	List<Trade> endOfMarket(SignalType signalType);
	
	void setConfiguration(List<Parameter> paramList);
	
	public IPositionManager getPositionManager();
	public Double getAskPrice();
	public Double getBidPrice();
}
