package com.meca.trade.components;

import java.util.ArrayList;

import com.jpmorrsn.fbp.engine.Component;

public abstract class Indicator extends Component {

	protected ArrayList<Double> window;

	public Indicator() {
		super();
		window = new ArrayList<Double>();
	}

	protected Double average(){
		Double result = 0d;
		
		
		for(Double data:window){
			result += data; 
		} 
		
		return result/window.size();
	}
}
