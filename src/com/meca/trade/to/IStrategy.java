package com.meca.trade.to;

import java.io.Serializable;

import com.jpmorrsn.fbp.engine.Packet;

public interface IStrategy extends Serializable{
	public StrategyDecision execute(Packet[] pArray, PriceData price);
	public void addIndicator(String indicatorName, Integer indicatorPort);
	public String getIndicatorHeaders();
	public String getIndicatorData();
}
