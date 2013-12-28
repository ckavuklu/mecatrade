package com.meca.trade.networks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndicatorManager {

	
	HashMap<String,List<IndicatorParameter>> indicatorMap;
	
	public IndicatorManager() {
		indicatorMap = new HashMap<String,List<IndicatorParameter>>();
	}



	public HashMap<String, List<IndicatorParameter>> getIndicatorMap() {
		return indicatorMap;
	}


	public List<IndicatorParameter> getIndicatorList(String mapName) throws CloneNotSupportedException {
		List<IndicatorParameter> newList = new ArrayList<IndicatorParameter>(); 
		List<IndicatorParameter> list = indicatorMap.get(mapName);
		
		for(IndicatorParameter param:list){
			newList.add(new IndicatorParameter(param));
		}
		
		return newList;
	}

	public void addIndicator(IndicatorParameter indicatorParam) {
		
		List<IndicatorParameter> list = indicatorMap.get(indicatorParam.getNetworkName());
		
		if(list == null)
			list = new ArrayList<IndicatorParameter>();
		
		list.add(indicatorParam);
		
		indicatorMap.put(indicatorParam.getNetworkName(), list);
			
	}
	
}
