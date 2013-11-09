package com.meca.trade.to;

import com.jpmorrsn.fbp.engine.Packet;

public class SMAStrategy implements IStrategy {
	
	IndicatorSet shortSMA;
	IndicatorSet longSMA;
	
	Double previousShortSMA = Double.NaN;
	Double currentShortSMA = Double.NaN;
	Double previousLongSMA = Double.NaN;
	Double currentLongSMA = Double.NaN;


	public SMAStrategy(IndicatorSet shortSMA, IndicatorSet longSMA) {
		super();
		this.shortSMA = shortSMA;
		this.longSMA = longSMA;
	}


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		StrategyDecision decision = new StrategyDecision(DecisionType.KEEP, data);
		
		this.currentShortSMA = (Double)pArray[shortSMA.getMap().get("SMA")].getContent();
		this.currentLongSMA = (Double)pArray[longSMA.getMap().get("SMA")].getContent();
			
		if(!(previousShortSMA.isNaN() || previousLongSMA.isNaN())){
			
			
			if(previousShortSMA < previousLongSMA && currentShortSMA > currentLongSMA){
				decision = new StrategyDecision(DecisionType.LONG, data);
			} 
			
			if(previousShortSMA > previousLongSMA && currentShortSMA < currentLongSMA){
				decision = new StrategyDecision(DecisionType.SHORT, data);
			}
			
		}
		
		previousShortSMA  = currentShortSMA;
		previousLongSMA  = currentLongSMA;
		
		
		return decision;
	}

}
