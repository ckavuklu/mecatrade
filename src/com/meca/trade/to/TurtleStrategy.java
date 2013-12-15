package com.meca.trade.to;

import com.jpmorrsn.fbp.engine.Packet;

public class TurtleStrategy extends BaseStrategy{

	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		Packet a = pArray[set.getMap().get("LINE")];
		
		
		return null;
	}

}
