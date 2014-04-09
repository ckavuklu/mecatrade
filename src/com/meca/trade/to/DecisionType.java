package com.meca.trade.to;

import java.io.Serializable;

public enum DecisionType implements Serializable{
	
	LONG_ENTRY(1d),
	SHORT_ENTRY(2d),
	LONG_EXIT(3d),
	SHORT_EXIT(4d),
	KEEP(0d);
	
	
	private final Double value;

    private DecisionType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
