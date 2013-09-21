package com.meca.trade.to;

import java.io.Serializable;

public enum SignalType implements Serializable{
	En(0d),
	Ex(1d),
	Pt(2d),
	Rs(3d),
	Ps(4d);
	
	private final Double value;

    private SignalType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
