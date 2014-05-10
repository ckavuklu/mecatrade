package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

public class TurtleStrategy extends BaseStrategy{

	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		super.execute(pArray, data);
		
		
		return null;
	}

}
