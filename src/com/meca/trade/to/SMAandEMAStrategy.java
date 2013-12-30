package com.meca.trade.to;

import com.jpmorrsn.fbp.engine.Packet;

public class SMAandEMAStrategy extends BaseStrategy {
	
	
	Double previousShortSMA = Double.NaN;
	Double currentShortSMA = Double.NaN;
	Double previousLongSMA = Double.NaN;
	Double currentLongSMA = Double.NaN;
	
	
	
	Double previousShortEMA = Double.NaN;
	Double currentShortEMA = Double.NaN;
	Double previousLongEMA = Double.NaN;
	Double currentLongEMA = Double.NaN;


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		StrategyDecision decision = new StrategyDecision(DecisionType.KEEP, data);
		
		this.currentShortSMA = (Double)pArray[set.getMap().get("SMASHORT")].getContent();
		this.currentLongSMA = (Double)pArray[set.getMap().get("SMALONG")].getContent();
		
		this.currentShortEMA = (Double)pArray[set.getMap().get("EMASHORT")].getContent();
		this.currentLongEMA = (Double)pArray[set.getMap().get("EMALONG")].getContent();
			
		if(!(previousShortSMA.isNaN() || previousLongSMA.isNaN() || previousShortEMA.isNaN() || previousLongEMA.isNaN())){
			
			
			if((previousShortSMA < previousLongSMA && currentShortSMA > currentLongSMA) || ( previousShortEMA < previousLongEMA && currentShortEMA > currentLongEMA)){
				decision = new StrategyDecision(DecisionType.LONG, data);
			} 
			
			if((previousShortSMA > previousLongSMA && currentShortSMA < currentLongSMA) || (previousShortEMA > previousLongEMA && currentShortEMA < currentLongEMA)){
				decision = new StrategyDecision(DecisionType.SHORT, data);
			}
			
		}
		
		previousShortSMA  = currentShortSMA;
		previousLongSMA  = currentLongSMA;
		
		previousShortEMA  = currentShortEMA;
		previousLongEMA  = currentLongEMA;
		
		
		return decision;
	}

}
