package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class Order extends MecaObject{

	@Override
	public String toString() {
		return super.toString();
	}

	private List<Trade> tradeList = null;
	
	private void setTradeList(List<Trade> tradeList) {
		this.tradeList = tradeList;
	}

	public Order(List<Trade> tradeList) {
		this.tradeList = tradeList;
	}
	
	public Order() {
		tradeList = new ArrayList<Trade>();
	}
	
	public void addTrade(Trade act){
		tradeList.add(act);
	}
	
	public int getTradeCount(){
		return tradeList.size();
	}

	public List<Trade> getTradeList() {
		return tradeList;
	}


}
