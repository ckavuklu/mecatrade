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



	public void addIndicator(IndicatorParameter indicatorParam) {
		
		List<IndicatorParameter> list = indicatorMap.get(indicatorParam.getNetworkName());
		
		if(list == null)
			list = new ArrayList<IndicatorParameter>();
		
		list.add(indicatorParam);
		
		indicatorMap.put(indicatorParam.getNetworkName(), list);
			
	}
	
}
