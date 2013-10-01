package com.meca.trade.to;

import java.util.List;

public class AccountManager extends MecaObject implements IAccountManager{

	private List<IAccount> accountList;

	public AccountManager(List<IAccount> accountList) {
		super();
		this.accountList = accountList;
	}
	
	/**
	 * TODO: We assumed that only one account exist with the same currency type in the same account manager
	 * 
	 * 
	 * 
	 */
	@Override
	public Double getBalance(CurrencyType currency) {
		Double balance = 0d;
		
		for (IAccount account : accountList) {
			if (account.getCurrency() == currency && account.isTradable()) {
				balance += account.getBalance();
			}
		}
		
		return null;
	}

	@Override
	public IAccount withdraw(CurrencyType currency, Double amount, AccountActionType type,) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAccount deposit(CurrencyType currency, Double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
}
