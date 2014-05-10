package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

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
		super.execute(pArray, data);
		StrategyDecision decision = new StrategyDecision(DecisionType.KEEP, data);
		
		this.currentShortSMA = (Double)pArray[set.getMap().get("SMASHORT-v")].getContent();
		this.currentLongSMA = (Double)pArray[set.getMap().get("SMALONG-v")].getContent();
		
		this.currentShortEMA = (Double)pArray[set.getMap().get("EMASHORT-v")].getContent();
		this.currentLongEMA = (Double)pArray[set.getMap().get("EMALONG-v")].getContent();
			
		if(!(previousShortSMA.isNaN() || previousLongSMA.isNaN() || previousShortEMA.isNaN() || previousLongEMA.isNaN())){
			
			
			if((previousShortSMA < previousLongSMA && currentShortSMA > currentLongSMA) 
					|| 
			   ( previousShortEMA < previousLongEMA && currentShortEMA > currentLongEMA)){
				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);
			} 
			
			if((previousShortSMA > previousLongSMA && currentShortSMA < currentLongSMA) 
					|| 
			   (previousShortEMA > previousLongEMA && currentShortEMA < currentLongEMA)){
				decision = new StrategyDecision(DecisionType.SHORT_ENTRY, data);
			}
			
		}
		
		previousShortSMA  = currentShortSMA;
		previousLongSMA  = currentLongSMA;
		
		previousShortEMA  = currentShortEMA;
		previousLongEMA  = currentLongEMA;
		
		
		return decision;
	}

}
