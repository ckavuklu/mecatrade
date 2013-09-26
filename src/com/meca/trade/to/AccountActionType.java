package com.meca.trade.to;

import java.io.Serializable;

public enum AccountActionType implements Serializable{
	BLOCK(0d),
	REAL(1d);
	
	private final Double value;

    private AccountActionType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
