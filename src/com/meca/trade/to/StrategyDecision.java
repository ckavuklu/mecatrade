package com.meca.trade.to;

public class StrategyDecision extends MecaObject{
	@Override
	public String toString() {
		
		return "DecisionType:"+decision+" QuoteType:"+quote;
	}

	private DecisionType decision;
	private MarketType quote;

	public StrategyDecision(DecisionType decision, MarketType quote) {
		super();
		this.decision = decision;
		this.quote = quote;
	}

	public DecisionType getDecision() {
		return decision;
	}

	public void setDecision(DecisionType decision) {
		this.decision = decision;
	}

	public MarketType getQuote() {
		return quote;
	}

	public void setQuote(MarketType quote) {
		this.quote = quote;
	}

}
