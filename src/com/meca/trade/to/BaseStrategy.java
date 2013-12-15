package com.meca.trade.to;


public abstract class BaseStrategy implements IStrategy {
	
	IndicatorSet set;
	
	public BaseStrategy(IndicatorSet set) {
		super();
		this.set = set;
	}
	
	public BaseStrategy() {
		super();
		set = new IndicatorSet();
	}


	@Override
	public void addIndicator(String indicatorName, Integer indicatorPort) {
		set.addIndicator(indicatorName, indicatorPort);
	}

	

}
