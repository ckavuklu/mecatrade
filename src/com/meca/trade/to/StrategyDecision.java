package com.meca.trade.to;

public class StrategyDecision extends MecaObject{
	@Override
	public String toString() {
		
		return "DecisionType:"+decision+" QuoteType:"+quote;
	}

	private DecisionType decision;
	private QuoteType quote;

	public StrategyDecision(DecisionType decision, QuoteType quote) {
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

	public QuoteType getQuote() {
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
	}

}
