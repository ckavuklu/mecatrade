package com.meca.trade.to;

import java.io.Serializable;
import java.util.List;

public interface ITrader extends Serializable {
	List<Trade> evaluateStrategyDecisions(List<StrategyDecision> decisionList);
}
