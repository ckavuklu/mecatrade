package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants.TREND;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

	
	public class StrategyParabolic  extends BaseStrategy{
	
	private final static Integer SAR_SIGNAL_THRESHOLD = 5;
	private final static Integer ADX_THRESHOLD = 15;
	
	private final static Integer ADX_SIGNAL_ORDER = 150;
		
	Double currentSAR = 0d;
	Integer sarSignalOrder = 0;
	TREND sarTrend = TREND.NONTRENDING;
	TREND adxTrend = TREND.NONTRENDING;
	
	
	Double currentADX = 0d;
	Double adxSignalOrder = 0d;
	Double previouspDI = Double.NaN;
	Double currentpDI = Double.NaN;
	Double previousmDI = Double.NaN;
	Double currentmDI = Double.NaN;


	Double currentDLine = Double.NaN;
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("sar=");
		builder.append(currentSAR);
		
		builder.append("\r\n");

		return builder.toString();
	}

	private void setTrend(TREND currentTrend ){
		if(sarTrend == currentTrend){
			sarSignalOrder++;
		}else{
			sarSignalOrder=1;
		}
		
		sarTrend = currentTrend;
	}

	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		super.execute(pArray, data);
		StrategyDecision decision  = null;
		
		this.currentSAR = (Double)pArray[set.getMap().get("SAR")].getContent();
		this.currentADX = (Double)pArray[set.getMap().get("ADX")].getContent();
		
		this.currentpDI = (Double)pArray[set.getMap().get("PDI")].getContent();
		this.currentmDI = (Double)pArray[set.getMap().get("MDI")].getContent();
		
		adxSignalOrder++;
		
		if(!(previouspDI.isNaN() || previousmDI.isNaN()) && adxSignalOrder>=ADX_SIGNAL_ORDER){
		
			if(this.currentSAR >= data.getHigh()){
				setTrend(TREND.DOWN);
			}
			
			if(this.currentSAR <= data.getLow()) {
				setTrend(TREND.UP);
			}
			
			if(previouspDI < previousmDI && currentpDI > currentmDI ){
				adxTrend = TREND.UP;

			} 
			
			if(previouspDI > previousmDI && currentpDI < currentmDI ){
				adxTrend = TREND.DOWN; 
			}
			
			if(adxTrend == TREND.UP && sarTrend == TREND.UP && currentADX>=ADX_THRESHOLD){
				decision = new StrategyDecision(DecisionType.SHORT_EXIT, data);
				
			}
			
			
			if(adxTrend == TREND.DOWN && sarTrend == TREND.DOWN && currentADX>=ADX_THRESHOLD){
				decision = new StrategyDecision(DecisionType.SHORT_ENTRY, data);
			}

		}
		
		previouspDI  = currentpDI;
		previousmDI  = currentmDI;
		
		if (decision == null)
			decision = new StrategyDecision(DecisionType.KEEP, data);
		
		return decision;
	}

}

