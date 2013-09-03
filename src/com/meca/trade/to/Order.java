package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class Order extends MecaObject{

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	private List<Action> actionList = null;
	
	public Order() {
		actionList = new ArrayList<Action>();
	}
	
	public Order(DecisionType decision, QuoteType quote, Double lot) {
		actionList = new ArrayList<Action>();
		actionList.add(new Action(decision, quote, lot));
	}
	
	public void addAction(Action act){
		actionList.add(act);
	}
	
	public int getActionCount(){
		return actionList.size();
	}

	public List<Action> getActionList() {
		return actionList;
	}


}
