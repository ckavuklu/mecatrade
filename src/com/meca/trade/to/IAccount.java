package com.meca.trade.to;

import java.io.Serializable;


public interface IAccount  extends Serializable{
	
	
	public CurrencyType getCurrency();

	public String getAccountNo();

	public Double getBalance();
	
	public void setBalance(Double amount);
	
	public AccountStatusType getStatus();
	
	public String toString();

}
