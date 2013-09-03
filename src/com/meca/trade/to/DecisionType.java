package com.meca.trade.to;

import java.io.Serializable;

public enum DecisionType implements Serializable{
	LONG(1d),
	SHORT(2d),
	KEEP(0d);
	
	private final Double value;

    private DecisionType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
