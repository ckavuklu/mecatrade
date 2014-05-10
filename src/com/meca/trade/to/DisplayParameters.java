package com.meca.trade.to;


public class DisplayParameters extends MecaObject {
	@Override
	public String toString() {
		return displayParameters;
	}

	private String displayParameters = null;

	public String getDisplayParameters() {
		return displayParameters;
	}

	public void setDisplayParameters(String displayParameters) {
		this.displayParameters = displayParameters;
	}

	public DisplayParameters(String displayParameters) {
		super();
		this.displayParameters = displayParameters;
	}

	
}
