package com.meca.trade.to;

import java.io.Serializable;


public interface IAccount  extends Serializable{
	
	
	public CurrencyType getCurrency();

	public String getAccountNo();

	public Double getBalance();

	public IAccount withdraw(Double amount) ;
	
	public IAccount deposit(Double amount) ;
	
	public boolean isTradable();
	
	public AccountStatusType getStatus();

}
