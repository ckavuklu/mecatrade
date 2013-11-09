package com.meca.trade.to;

import java.io.Serializable;
import java.util.HashMap;

public class IndicatorSet implements Serializable {
	HashMap<String, Integer> map;


	public HashMap<String, Integer> getMap() {
		return map;
	}


	public void addIndicator(String key, Integer value) {
		this.map.put(key,value);
	}


	public IndicatorSet(HashMap<String, Integer> map) {
		super();
		this.map = map;
	}
	
	public IndicatorSet() {
		super();
		this.map = new HashMap<String,Integer>();
	}
	
	

}
