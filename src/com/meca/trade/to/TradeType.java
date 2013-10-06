package com.meca.trade.to;

import java.io.Serializable;

public enum TradeType implements Serializable{
	SELL(0d),
	SEXIT(1d),
	BUY(2d),
	LEXIT(3d);
	
	private final Double value;

    private TradeType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
