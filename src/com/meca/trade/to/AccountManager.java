package com.meca.trade.to;

import java.util.List;

public class AccountManager extends MecaObject implements IAccountManager{

	private List<IAccount> accountList;

	public AccountManager(List<IAccount> accountList) {
		super();
		this.accountList = accountList;
	}
	
	@Override
	public Double getBalance(CurrencyType currency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAccount withdraw(CurrencyType currency, Double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAccount deposit(CurrencyType currency, Double amount) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
}
