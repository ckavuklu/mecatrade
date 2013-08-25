package com.meca.trade.components;

import java.util.ArrayList;
import com.jpmorrsn.fbp.engine.Component;

public abstract class Indicator extends Component {

	protected ArrayList<Double> window;

	public Indicator() {
		super();
		window = new ArrayList<Double>();
	}

}
