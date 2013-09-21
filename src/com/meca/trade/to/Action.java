package com.meca.trade.to;

public class Action extends StrategyDecision {
	@Override
	public String toString() {
		
		return super.toString() + " Lot:"+lot;
	}

	private Double lot;

	public Action(DecisionType decision, MarketType quote, Double lot) {
		super(decision, quote);
		this.lot = lot;
	}

	public Double getLot() {
		return lot;
	}

	public void setLot(Double lot) {
		this.lot = lot;
	}

}
