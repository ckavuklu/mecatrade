package com.meca.trade.to;

import java.io.Serializable;

public enum CurrencyType implements Serializable{
	TRY(0d),
	USD(1d),
	EUR(2d);
	
	private final Double value;

    private CurrencyType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
