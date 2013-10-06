package com.meca.trade.to;

import java.io.Serializable;


public interface IAccount  extends Serializable{
	
	
	public CurrencyType getCurrency();

	public String getAccountNo();

	public Double getBalance();

	public boolean withdrawBlocked(Double amount) ;
	
	public boolean withdrawRealized(Double blockedAmount,Double realizedAmount) ;
	
	public boolean deposit(Double amount) ;
	
	public boolean releaseBlock(Double amount) ;
	
	public boolean isTradable();
	
	public AccountStatusType getStatus();
	
	public String toString();

}
