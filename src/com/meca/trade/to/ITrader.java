package com.meca.trade.to;

import java.io.Serializable;
import java.util.List;

public interface ITrader extends Serializable, IPriceData {
	List<Trade> evaluateStrategyDecisions(List<StrategyDecision> decisionList);
	
	List<Trade> endOfMarket();
}
