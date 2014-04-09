package com.meca.trade.to;

import com.jpmorrsn.fbp.engine.Packet;

public class StrategyIzmir  extends BaseStrategy{
	

	Double previousKLine = Double.NaN;
	Double currentKLine = Double.NaN;
	Double previousDLine = Double.NaN;
	Double currentDLine = Double.NaN;
	
	Double previousRSI = Double.NaN;
	Double currentRSI = Double.NaN;
	
	Double overboughtRSIValue = 80d;
	Double midLevelRSIValue = 50d;
	Double oversoldRSIValue = 20d;
	Double overboughtStochasticValue = 70d;
	Double oversoldStochasticValue = 30d;
	
	Double previousShortEMA = Double.NaN;
	Double currentShortEMA = Double.NaN;
	Double previousLongEMA = Double.NaN;
	Double currentLongEMA = Double.NaN;
	
	
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
		builder.append("overboughtStochasticValue=");
		builder.append(overboughtStochasticValue);
		builder.append(" ");
		builder.append("oversoldStochasticValue=");
		builder.append(oversoldStochasticValue);
		builder.append(" ");
		builder.append("previousRSI=");
		builder.append(previousRSI);
		builder.append(" ");
		builder.append("currentRSI=");
		builder.append(currentRSI);
		builder.append(" ");
		builder.append("overboughtRSIValue=");
		builder.append(overboughtRSIValue);
		builder.append(" ");
		builder.append("oversoldRSIValue=");
		builder.append(oversoldRSIValue);
		builder.append(" ");
		builder.append("previousShortEMA=");
		builder.append(previousShortEMA);
		builder.append(" ");
		builder.append("currentShortEMA=");
		builder.append(currentShortEMA);
		builder.append(" ");
		builder.append("previousLongEMA=");
		builder.append(previousLongEMA);
		builder.append(" ");
		builder.append("currentLongEMA=");
		builder.append(currentLongEMA);

		builder.append("\r\n");

		return builder.toString();
	}


	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		
		StrategyDecision decision  = null;
		
		this.currentKLine = (Double)pArray[set.getMap().get("KLINE")].getContent();
		this.currentDLine = (Double)pArray[set.getMap().get("DLINE")].getContent();
		
		this.currentRSI = (Double)pArray[set.getMap().get("RSI")].getContent();

		this.currentShortEMA = (Double)pArray[set.getMap().get("EMASHORT")].getContent();
		this.currentLongEMA = (Double)pArray[set.getMap().get("EMALONG")].getContent();
		
		
		if(!(previousShortEMA.isNaN() || previousLongEMA.isNaN() || previousKLine.isNaN() || previousDLine.isNaN())){
			
			
			
			if(		(previousShortEMA < previousLongEMA && currentShortEMA > currentLongEMA) && 
					(previousKLine < currentKLine && previousDLine < currentDLine ) &&
					(currentDLine < overboughtStochasticValue) && 
					(currentRSI > midLevelRSIValue)
				) 

				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);

				
			if(		(previousShortEMA > previousLongEMA && currentShortEMA < currentLongEMA) && 
					(previousKLine > currentKLine && previousDLine > currentDLine ) &&
					(currentDLine > oversoldStochasticValue) && 
					(currentRSI < midLevelRSIValue)
				) 

				if (decision == null)
					decision = new StrategyDecision(DecisionType.SHORT_ENTRY, data);
				else decision.getDecisionList().add(DecisionType.SHORT_ENTRY);
			
			
			if(		(previousShortEMA > previousLongEMA && currentShortEMA < currentLongEMA) ||
					(currentRSI < midLevelRSIValue)
				) 
				if (decision == null)
					decision = new StrategyDecision(DecisionType.LONG_EXIT, data);
				else decision.getDecisionList().add(DecisionType.LONG_EXIT);
			
			
			if(		(previousShortEMA < previousLongEMA && currentShortEMA > currentLongEMA) || 
					(currentRSI > midLevelRSIValue)
				) 

				if (decision == null)
					decision = new StrategyDecision(DecisionType.SHORT_EXIT, data);
				else decision.getDecisionList().add(DecisionType.SHORT_EXIT);
			
			
		}
		
		/*System.out.println("StochasticStrategy values:");s
		System.out.println(this);*/
		
		
		previousKLine  = currentKLine;
		previousDLine  = currentDLine;
		previousRSI = currentRSI;
		
		previousShortEMA = currentShortEMA;
		previousLongEMA = currentLongEMA;


		if (decision == null)
			decision = new StrategyDecision(DecisionType.KEEP, data);
		
		return decision;
	}

}
