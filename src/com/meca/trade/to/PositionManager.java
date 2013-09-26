package com.meca.trade.to;

import java.util.ArrayList;
import java.util.List;

public class PositionManager extends MecaObject implements IPositionManager{

	private List<IPosition> positionList;

	public PositionManager(List<IPosition> positionList) {
		super();
		this.positionList = positionList;
		
		if(this.positionList == null){
			this.positionList = new ArrayList<IPosition>();
		}
	}
	
	private IPosition getPosition(Integer positionNo){
		IPosition result = null;
		if(positionNo == null){
			Integer posNo = this.positionList.size();
			result = new Position(posNo,TradeStatusType.OPEN);
		}else{
			result = positionList.get(positionNo);
		}
		
		return result;
			
	}
	
	

	@Override
	public Trade addTrade(Trade data) {

		return getPosition(data.getPositionNo()).addTrade(data);
	}

	@Override
	public List<IPosition> getPositions() {
		
		return positionList;
	}

	@Override
	public Double getTotalRisk() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
