package com.meca.trade.to;

import java.io.Serializable;

public enum TradeStatusType implements Serializable{
	OPEN(1d),
	CLOSE(0d);
	
	
	private final Double value;

    private TradeStatusType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
