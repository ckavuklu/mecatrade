package com.meca.trade.to;

import java.util.Random;

public class Account {
	private CurrencyType currency = null;
	private String accountNo = null;
	private Double balance = null;
	
	public CurrencyType getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyType currency) {
		this.currency = currency;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Account(CurrencyType currency, String accountNo, Double balance) {
		super();
		this.currency = currency;
		this.accountNo = accountNo;
		this.balance = balance;
	}
	
	public Account(CurrencyType currency, Double balance) {
		super();
		this.currency = currency;
		this.balance = balance;
		this.accountNo = String.valueOf(new Random().nextDouble());
	}
	
	public Account(CurrencyType currency) {
		super();
		this.currency = currency;
		this.balance = 1000d;
		this.accountNo = String.valueOf(new Random().nextDouble());
	}

}
