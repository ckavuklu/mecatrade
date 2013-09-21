package com.meca.trade.to;

import java.io.Serializable;

public enum MarketType implements Serializable{
	EURUSD(1d),
	USDJPY(3d),
	USDTRY(0d),
	GBPUSD(2d),
	BIST(3d);
	
	private final Double value;

    private MarketType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
