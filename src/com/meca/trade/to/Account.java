package com.meca.trade.to;

import java.text.DecimalFormat;

public class Account implements IAccount {
	
	
	private CurrencyType currency = null;
	private String accountNo = null;
	private Double balance = null;
	private AccountStatusType status = null;
	
	
	@Override
	public String toString() {
		DecimalFormat format = new DecimalFormat("#.##");
		StringBuilder builder = new StringBuilder();
		
		builder.append("currency=");
		builder.append(currency);
		builder.append(" ");
		builder.append("accountNo=");
		builder.append(accountNo);
		builder.append(" ");
		builder.append("balance=");
		builder.append(format.format(balance));
		builder.append(" ");
		builder.append("status=");
		builder.append(status);
		
		
		return builder.toString();
	}

	
	public Account(CurrencyType currency, String accountNo, Double balance,
			AccountStatusType status) {
		super();
		this.currency = currency;
		this.accountNo = accountNo;
		this.balance = balance;
		this.status = status;
		
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


	@Override
	public AccountStatusType getStatus() {
		return status;
	}


	@Override
	public void setBalance(Double amount) {
		
		if(amount > 0 && getStatus() == AccountStatusType.OPEN)
		{
			balance = amount;
		}
		
	}

}
