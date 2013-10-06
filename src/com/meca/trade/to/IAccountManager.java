package com.meca.trade.to;

public interface IAccountManager {
	public Double getBalance(CurrencyType currency);

	public boolean withdraw(Trade trade);
	
	public boolean deposit(Trade trade) ;
	
	public boolean cancel(Trade trade) ;
	
	public String toString();
	

}
