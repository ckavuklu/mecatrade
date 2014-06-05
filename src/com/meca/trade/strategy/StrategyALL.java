package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants.TREND;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

	
	public class StrategyALL  extends BaseStrategy{
	
		
	Double currentSAR = Double.NaN;
	Double previousSAR = Double.NaN;
	Double previousLow = Double.NaN;
	Double previousHigh = Double.NaN;
	
	
	TREND sarTrend = TREND.NONTRENDING;
	
	
	Double currentADX = 0d;
	Double previouspDI = Double.NaN;
	Double currentpDI = Double.NaN;
	Double previousmDI = Double.NaN;
	Double currentmDI = Double.NaN;

	Double previousShortSMA = Double.NaN;
	Double currentShortSMA = Double.NaN;
	Double previousLongSMA = Double.NaN;
	Double currentLongSMA = Double.NaN;


	Double previousKLine = Double.NaN;
	Double currentKLine = Double.NaN;
	Double previousDLine = Double.NaN;
	Double currentDLine = Double.NaN;
	
	Double stochasticOverboughtValue = 70d;
	Double stochasticOversoldValue = 30d;
	
	Double previousRSI = Double.NaN;
	Double currentRSI = Double.NaN;
	
	Double overboughtRSIValue = 70d;
	Double midLevelRSIValue = 50d;
	Double oversoldRSIValue = 30d;

	
	Double RSIwght = Double.NaN;
	Double SMAwght = Double.NaN;
	Double ADXwght = Double.NaN;
	Double SARwght = Double.NaN;
	Double STOCHASTICwght = Double.NaN;
	Double CONFlevel = Double.NaN;

	Double RSIVote = Double.NaN;
	Double SMAVote = Double.NaN;
	Double ADXVote = Double.NaN;
	Double SARVote = Double.NaN;
	Double STOCHASTICVote = Double.NaN;
	
	Double DECISION = 0d;
	
	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		
		
		super.execute(pArray, data);
		StrategyDecision decision  = null;
		
		this.currentSAR = (Double)pArray[set.getMap().get("SAR")].getContent();
		this.currentADX = (Double)pArray[set.getMap().get("ADX")].getContent();
		
		this.currentpDI = (Double)pArray[set.getMap().get("PDI")].getContent();
		this.currentmDI = (Double)pArray[set.getMap().get("MDI")].getContent();
		
		this.currentShortSMA = (Double)pArray[set.getMap().get("SMASHORT")].getContent();
		this.currentLongSMA = (Double)pArray[set.getMap().get("SMALONG")].getContent();
			
		this.currentKLine = (Double)pArray[set.getMap().get("KLINE")].getContent();
		this.currentDLine = (Double)pArray[set.getMap().get("DLINE")].getContent();
		
		this.currentRSI = (Double)pArray[set.getMap().get("RSI")].getContent();
	
		this.RSIwght = (Double)pArray[set.getMap().get("RSI_Weight")].getContent();
		this.SARwght = (Double)pArray[set.getMap().get("SAR_Weight")].getContent();
		this.ADXwght = (Double)pArray[set.getMap().get("ADX_Weight")].getContent();
		this.SMAwght = (Double)pArray[set.getMap().get("SMA_Weight")].getContent();
		this.STOCHASTICwght = (Double)pArray[set.getMap().get("Stochastic_Weight")].getContent();
			
		this.CONFlevel = (Double)pArray[set.getMap().get("Confidence_Level")].getContent();
	
		// SMA DECISION
		if (!(previousShortSMA.isNaN() || previousLongSMA.isNaN())) {
			if (previousShortSMA < previousLongSMA
					&& currentShortSMA > currentLongSMA) {
				DECISION += SMAwght;
			}
			if (previousShortSMA > previousLongSMA
					&& currentShortSMA < currentLongSMA) {
				DECISION -= SMAwght;
			}
		}
		
		// ADX DECISION
				if (!(previouspDI.isNaN() || previousmDI.isNaN())) {
					if (previouspDI < previousmDI
							&& currentpDI > currentmDI) {
						DECISION += ADXwght;
					}
					if (previouspDI > previousmDI
							&& currentpDI < currentmDI) {
						DECISION -= ADXwght;
					}
				}
		
		// STOCHASTIC DECISION
		if (!(previousKLine.isNaN() || previousDLine.isNaN())) {
			if (previousKLine < previousDLine && currentKLine > currentDLine
					&& currentDLine <= stochasticOversoldValue ) {
				DECISION += STOCHASTICwght;
			}
			if (previousKLine > previousDLine && currentKLine < currentDLine
					&& currentDLine >= stochasticOverboughtValue) {
				DECISION -= STOCHASTICwght;
			}
		}
		
		
		//RSI DECISION
		if(currentRSI >= overboughtRSIValue){
			DECISION -= RSIwght;
		}else if(currentRSI <= oversoldRSIValue){
			DECISION += RSIwght;
		} 
		
		
		//SAR DECISION
		if (!previousSAR.isNaN()) {

			if (currentSAR < data.getLow() && previousSAR > previousHigh)
				DECISION += SARwght;
			else if (currentSAR > data.getHigh() && previousSAR < previousLow)
				DECISION -= SARwght;
		}		
		
		DECISION = DECISION / 5;
		
		if(Math.abs(DECISION) > CONFlevel) {
			if(DECISION>0)
				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);
			else 
				decision = new StrategyDecision(DecisionType.LONG_EXIT, data);
		}
		else 
			decision = new StrategyDecision(DecisionType.KEEP, data);

		previousmDI = currentmDI;
		previouspDI = currentpDI;
		previousShortSMA  = currentShortSMA;
		previousLongSMA  = currentLongSMA;
		previousKLine  = currentKLine;
		previousDLine  = currentDLine;
		previousSAR = currentSAR;
		previousLow = data.getLow();
		previousHigh = data.getHigh();

		return decision;
	}

}

