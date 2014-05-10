package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.IndicatorSet;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

public class StochasticStrategy  extends BaseStrategy{
	
	

	IndicatorSet kLine;
	IndicatorSet dLine;
	
	Double previousKLine = Double.NaN;
	Double currentKLine = Double.NaN;
	Double previousDLine = Double.NaN;
	


	Double currentDLine = Double.NaN;
	
	Double overboughtValue;
	Double oversoldValue;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("previousKLine=");
		builder.append(previousKLine);
		builder.append(" ");
		builder.append("previousDLine=");
		builder.append(previousDLine);
		builder.append(" ");
		builder.append("currentKLine=");
		builder.append(currentKLine);
		builder.append(" ");
		builder.append("currentDLine=");
		builder.append(currentDLine);
		builder.append(" ");
		builder.append("overboughtValue=");
		builder.append(overboughtValue);
		builder.append(" ");
		builder.append("oversoldValue=");
		builder.append(oversoldValue);
		builder.append("\r\n");

		return builder.toString();
	}

	

	public StochasticStrategy(IndicatorSet kLine, IndicatorSet dLine, Double overboughtValue, Double oversoldValue) {
		super();
		this.dLine = dLine;
		this.kLine = kLine;
		this.overboughtValue = overboughtValue;
		this.oversoldValue = oversoldValue;
	}


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		super.execute(pArray, data);
		StrategyDecision decision = new StrategyDecision(DecisionType.KEEP, data);
		
		this.currentKLine = (Double)pArray[kLine.getMap().get("KLINE-p")].getContent();
		this.currentDLine = (Double)pArray[dLine.getMap().get("DLINE-p")].getContent();
			
		if(!(previousKLine.isNaN() || previousDLine.isNaN())){
			
			
			if(previousKLine < previousDLine && currentKLine > currentDLine && currentDLine <= oversoldValue){
				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);
			} 
			
			if(previousKLine > previousDLine && currentKLine < currentDLine && currentDLine >= overboughtValue){
				decision = new StrategyDecision(DecisionType.SHORT_ENTRY, data);
			}
			
		}
		
		/*System.out.println("StochasticStrategy values:");
		System.out.println(this);*/
		
		
		previousKLine  = currentKLine;
		previousDLine  = currentDLine;
		
		return decision;
	}

}
