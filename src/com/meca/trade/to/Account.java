package com.meca.trade.to;

public class Account implements IAccount {
	private CurrencyType currency = null;
	private String accountNo = null;
	private Double balance = null;
	private Double blocked = null;
	private AccountStatusType status = null;
	private boolean tradable = false;
	
	
	public Account(CurrencyType currency, String accountNo, Double balance,
			AccountStatusType status, boolean tradable) {
		super();
		this.currency = currency;
		this.accountNo = accountNo;
		this.balance = balance;
		this.status = status;
		this.tradable = tradable;
		this.blocked = 0d;
	}

	private void setStatus(AccountStatusType status) {
		this.status = status;
	}

	public CurrencyType getCurrency() {
		return currency;
	}

	private void setCurrency(CurrencyType currency) {
		this.currency = currency;
	}

	public String getAccountNo() {
		return accountNo;
	}

	private void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Double getBalance() {
		return balance;
	}

	private void setBalance(Double balance) {
		this.balance = balance;
	}


	@Override
	public IAccount withdraw(Double amount) {
		
		if(getBalance() >= amount && amount > 0 && getStatus() == AccountStatusType.OPEN)
			setBalance(getBalance() - amount);
		
		return this;
	}

	@Override
	public IAccount deposit(Double amount) {
		if(amount > 0 && getStatus() == AccountStatusType.OPEN)
			setBalance(getBalance() + amount);
		
		return this;
	}

	private void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	@Override
	public boolean isTradable() {
		return tradable;
	}

	@Override
	public AccountStatusType getStatus() {
		return status;
	}

}
