package com.meca.trade.to;

public interface IAccountManager {
	public Double getBalance(CurrencyType currency);

	public IAccount withdraw(CurrencyType currency, Double amount, AccountActionType type, Integer tradeNo, Integer positionNo);
	
	public IAccount deposit(CurrencyType currency, Double amount, AccountActionType type, Integer tradeNo, Integer positionNo) ;

}
