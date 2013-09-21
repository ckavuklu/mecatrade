package com.meca.trade.to;

import java.io.Serializable;

public enum TradeType implements Serializable{
	Sell(0d),
	SExit(1d),
	Buy(2d),
	Lexit(3d);
	
	private final Double value;

    private TradeType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
