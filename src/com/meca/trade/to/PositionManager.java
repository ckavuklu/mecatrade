package com.meca.trade.to;

import java.util.List;

public class PositionManager extends MecaObject implements IPositionManager{

	private List<IPosition> positionList;

	public PositionManager(List<IPosition> positionList) {
		super();
		this.positionList = positionList;
	}

	@Override
	public Trade addTrade(Trade data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IPosition> getPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalRisk() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
