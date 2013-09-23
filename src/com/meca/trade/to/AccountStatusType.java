package com.meca.trade.to;

import java.io.Serializable;

public enum AccountStatusType implements Serializable{
	
	OPEN(1d),
	CLOSE(2d),
	SUSPENDED(0d);
	
	
	private final Double value;

    private AccountStatusType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
}
