package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class StrategyDecision extends MecaObject{
	
	private List<DecisionType> decisionList;
	public List<DecisionType> getDecisionList() {
		return decisionList;
	}

	public void setDecisionList(List<DecisionType> decisionList) {
		this.decisionList = decisionList;
	}




	private PriceData price;
	
	public PriceData getPrice() {
		return price;
	}

	private void setPrice(PriceData price) {
		this.price = price;
	}

	public StrategyDecision(DecisionType decision, PriceData price) {
		super();
		this.decisionList = new ArrayList<DecisionType>();
		decisionList.add(decision);
		this.price = price;
	}
	
	public StrategyDecision(List<DecisionType> decisionList, PriceData price) {
		super();
		this.decisionList = decisionList;
		this.price = price;
	}
	

	

	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		for(DecisionType decision:this.decisionList)
		 result.append("DecisionType: "+decision);
		
		return result.toString();
	}

}
