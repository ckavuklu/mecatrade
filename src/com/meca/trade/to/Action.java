package com.meca.trade.to;

public class Action {
	
	MarketType quote;
	DecisionType decision;
	
	@Override
	public String toString() {
		
		return "DecisionType:"+decision + " Lot:"+lot;
	}

	private Double lot;

	public Action(DecisionType decision, MarketType quote, Double lot) {
		this.decision = decision;
		this.lot = lot;
		this.quote = quote;
	}

	public Double getLot() {
		return lot;
	}

	public void setLot(Double lot) {
		this.lot = lot;
	}

}
