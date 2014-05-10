package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

	
	public class StrategyParabolic  extends BaseStrategy{
	

	Double currentSAR = 0d;
	
	
	
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("sar=");
		builder.append(currentSAR);
		
		builder.append("\r\n");

		return builder.toString();
	}


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		super.execute(pArray, data);
		StrategyDecision decision  = null;
		
		this.currentSAR = (Double)pArray[set.getMap().get("SAR")].getContent();
		
		if (decision == null)
			decision = new StrategyDecision(DecisionType.KEEP, data);
		
		return decision;
	}

}

