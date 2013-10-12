package com.meca.trade.to;

import java.io.Serializable;

public enum MarketType implements Serializable{
	EURUSD(1d),
	USDTRY(0d);
	
	private final Double value;

    private MarketType(Double value) {
        this.value = value;
    }
    public Double getValue() {
        return value;
    }
    
    public CurrencyType getBaseCurrency(){
    	CurrencyType result = null;
    	switch(this){
	    	case EURUSD : {
	    		result = CurrencyType.EUR;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = CurrencyType.USD;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
    	}
    	
    	return result;

    }
    
    public Integer getLotSize(){
    	return 1000;

    }
    
    public CurrencyType getQuoteCurrency(){
    	CurrencyType result = null;
    	switch(this){
	    	case EURUSD : {
	    		result = CurrencyType.USD;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = CurrencyType.TRY;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
    	}
    	
    	return result;
    	
    }
}
