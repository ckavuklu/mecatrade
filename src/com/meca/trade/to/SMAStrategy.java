package com.meca.trade.to;

import com.jpmorrsn.fbp.engine.Packet;

public class SMAStrategy extends BaseStrategy {
	
	
	Double previousShortSMA = Double.NaN;
	Double currentShortSMA = Double.NaN;
	Double previousLongSMA = Double.NaN;
	Double currentLongSMA = Double.NaN;


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		StrategyDecision decision = new StrategyDecision(DecisionType.KEEP, data);
		
		this.currentShortSMA = (Double)pArray[set.getMap().get("SMASHORT")].getContent();
		this.currentLongSMA = (Double)pArray[set.getMap().get("SMALONG")].getContent();
			
		if(!(previousShortSMA.isNaN() || previousLongSMA.isNaN())){
			
			
			if(previousShortSMA < previousLongSMA && currentShortSMA > currentLongSMA){
				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);
			} 
			
			if(previousShortSMA > previousLongSMA && currentShortSMA < currentLongSMA){
				decision = new StrategyDecision(DecisionType.SHORT_ENTRY, data);
			}
			
		}
		
		previousShortSMA  = currentShortSMA;
		previousLongSMA  = currentLongSMA;
		
		
		return decision;
	}

}
