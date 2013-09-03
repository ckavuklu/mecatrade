package com.meca.trade.to;

import java.io.Serializable;

public enum QuoteType implements Serializable{
	EURUSD(1d),
	USDJPY(3d),
	USDTRY(0d),
	GBPUSD(2d);
	
	private final Double value;

    private QuoteType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
