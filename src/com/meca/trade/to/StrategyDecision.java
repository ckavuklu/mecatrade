package com.meca.trade.to;

public class StrategyDecision extends MecaObject{
	
	private DecisionType decision;
	private PriceData price;
	
	public PriceData getPrice() {
		return price;
	}

	private void setPrice(PriceData price) {
		this.price = price;
	}

	public StrategyDecision(DecisionType decision, PriceData price) {
		super();
		this.decision = decision;
		this.price = price;
	}

	public DecisionType getDecision() {
		return decision;
	}

	public void setDecision(DecisionType decision) {
		this.decision = decision;
	}

	@Override
	public String toString() {
		
		return "DecisionType:"+decision;
	}

}
