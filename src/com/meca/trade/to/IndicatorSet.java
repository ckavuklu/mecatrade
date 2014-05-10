package com.meca.trade.to;

import java.io.Serializable;
import java.util.HashMap;

public class IndicatorSet implements Serializable {
	HashMap<String, Integer> map;
	HashMap<String, DisplayParameters> displayMap;
	

	public HashMap<String, DisplayParameters> getDisplayMap() {
		return displayMap;
	}


	public HashMap<String, Integer> getMap() {
		return map;
	}


	public void addIndicator(String key, String displayType, Integer value) {
		this.map.put(key,value);
		this.displayMap.put(key,new DisplayParameters(displayType));
	}
	
	public IndicatorSet() {
		super();
		this.map = new HashMap<String,Integer>();
		displayMap = new HashMap<String,DisplayParameters>();
	}
	
	

}
