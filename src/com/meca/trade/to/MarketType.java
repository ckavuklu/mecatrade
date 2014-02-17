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
    
    
    public Integer getPricePrecision(){
    	Integer result = 0;
    	switch(this){
	    	case EURUSD : {
	    		result = Constants.EURUSD_MARKET_PRICE_PRECISION;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = Constants.USDTRY_MARKET_PRICE_PRECISION;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
		}
    	return result;
    }
    
    
    public Integer getLotSize(){
    	Integer result = 0;
    	switch(this){
	    	case EURUSD : {
	    		result = Constants.EURUSD_LOT_SIZE;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = Constants.USDTRY_LOT_SIZE;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
		}
    	return result;

    }
    
    public Integer getLeverage(){
    	Integer result = 0;
    	switch(this){
	    	case EURUSD : {
	    		result = Constants.EURUSD_LEVERAGE;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = Constants.USDTRY_LEVERAGE;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
		}
    	return result;

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
    
    
    public Double getSpread(){
    	Double result = null;
    	switch(this){
	    	case EURUSD : {
	    		result = Constants.EURUSD_SPREAD;
	    		break;
	    	}
	    	
	    	case USDTRY : {
	    		result = Constants.USDTRY_SPREAD;
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
    	}
    	
    	return result;
    }
}
