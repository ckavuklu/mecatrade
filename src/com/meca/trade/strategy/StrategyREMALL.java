package com.meca.trade.strategy;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants.STRENGTH;
import com.meca.trade.to.Constants.TREND;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;

	
	public class StrategyREMALL  extends BaseStrategy{
	
		
	Double currentSAR = Double.NaN;
	Double previousSAR = Double.NaN;
	Double previousLow = Double.NaN;
	Double previousHigh = Double.NaN;
	
	TREND SARTrend = TREND.NONTRENDING;
	TREND RSITrend = TREND.NONTRENDING;
	TREND STOTrend = TREND.NONTRENDING;
	TREND SMATrend = TREND.NONTRENDING;
	TREND ADXTrend = TREND.NONTRENDING;
	
	STRENGTH SARStrength = STRENGTH.NORMAL;
	STRENGTH RSIStrength = STRENGTH.NORMAL;
	STRENGTH STOStrength = STRENGTH.NORMAL;
	STRENGTH SMAStrength = STRENGTH.NORMAL;
	STRENGTH ADXStrength = STRENGTH.NORMAL;
	
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
	
	
	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData data) {
		
		Double DECISION = 0d;

		String longEntryDecisionMakers = "";
		String longExitDecisionMakers = "";
		
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
				SMATrend = TREND.UP;
				DECISION += SMAwght;
				longEntryDecisionMakers += "SMA+";
			}
			if (previousShortSMA > previousLongSMA
					&& currentShortSMA < currentLongSMA) {
				SMATrend = TREND.DOWN;
				DECISION -= SMAwght;
				longExitDecisionMakers += "SMA+";

			}
		}
		
		// ADX DECISION
				if (!(previouspDI.isNaN() || previousmDI.isNaN())) {
					if (previouspDI < previousmDI
							&& currentpDI > currentmDI) {
						ADXTrend = TREND.UP;
						DECISION += ADXwght;
						longEntryDecisionMakers += "ADX+";

					}
					if (previouspDI > previousmDI
							&& currentpDI < currentmDI) {
						ADXTrend = TREND.DOWN;
						DECISION -= ADXwght;
						longExitDecisionMakers += "ADX+";

					}
				}
		
		// STOCHASTIC DECISION
		if (!(previousKLine.isNaN() || previousDLine.isNaN())) {
			if (previousKLine < previousDLine && currentKLine > currentDLine
					&& currentDLine <= stochasticOversoldValue ) {
				STOTrend = TREND.UP;
				DECISION += STOCHASTICwght;
				longEntryDecisionMakers += "STO+";
				

			}
			if (previousKLine > previousDLine && currentKLine < currentDLine
					&& currentDLine >= stochasticOverboughtValue) {
				STOTrend = TREND.DOWN;
				DECISION -= STOCHASTICwght;
				longExitDecisionMakers += "STO+";

			}
			
			if(Math.abs(currentKLine-currentDLine)>5) STOStrength = STRENGTH.STRONG;
			else STOStrength = STRENGTH.WEAK;
		}
		
		
		//RSI DECISION
		if(currentRSI >= overboughtRSIValue){
			RSITrend = TREND.DOWN;
			DECISION -= RSIwght;
			longEntryDecisionMakers += "RSI+";

		}else if(currentRSI <= oversoldRSIValue){
			RSITrend = TREND.UP;
			DECISION += RSIwght;
			longExitDecisionMakers += "RSI+";

		} else RSITrend = TREND.NONTRENDING;

		
		//SAR DECISION
		if (!previousSAR.isNaN()) {
			if (currentSAR < data.getLow() && previousSAR > previousHigh) {
				SARTrend = TREND.UP;
				DECISION += SARwght;
			}
			else if (currentSAR > data.getHigh() && previousSAR < previousLow){
				SARTrend = TREND.DOWN;
				DECISION -= SARwght;	
			}
			/*
			if (currentSAR < data.getLow() && currentADX > 25) {
					DECISION += SARwght;
					longEntryDecisionMakers += "SAR+";

			}
				else if (currentSAR > data.getHigh() && currentADX > 25) {
					DECISION -= SARwght;
					longExitDecisionMakers += "SAR+";

				}
			*/
		}		
		
		DECISION = DECISION / 5;
		
		if (Math.abs(DECISION) > CONFlevel) {
			if (DECISION > 0) {
				decision = new StrategyDecision(DecisionType.LONG_ENTRY, data);
				//System.out.println("Long Entry decision makers :" + longEntryDecisionMakers);

			} else {
				decision = new StrategyDecision(DecisionType.LONG_EXIT, data);
				//System.out.println("Long Exit decision makers :" + longExitDecisionMakers);

			}
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

