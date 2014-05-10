package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

public class SMAStrategy extends BaseStrategy {
	
	
	Double previousShortSMA = Double.NaN;
	Double currentShortSMA = Double.NaN;
	Double previousLongSMA = Double.NaN;
	Double currentLongSMA = Double.NaN;


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		super.execute(pArray, data);
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
