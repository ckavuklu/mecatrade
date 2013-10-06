package com.meca.trade.to;

public class Account implements IAccount {
	
	private CurrencyType currency = null;
	private String accountNo = null;
	private Double balance = null;
	private Double blocked = null;
	
	
	private AccountStatusType status = null;
	private boolean tradable = false;
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("currency=");
		builder.append(currency);
		builder.append(" ");
		builder.append("accountNo=");
		builder.append(accountNo);
		builder.append(" ");
		builder.append("balance=");
		builder.append(balance);
		builder.append(" ");
		builder.append("blocked=");
		builder.append(blocked);
		builder.append(" ");
		builder.append("status=");
		builder.append(status);
		builder.append(" ");
		builder.append("tradable=");
		builder.append(tradable);
		
		
		return builder.toString();
	}

	
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

	private Double getBlocked() {
		return blocked;
	}

	private void setBlocked(Double blocked) {
		this.blocked = blocked;
	}


	@Override
	public boolean withdrawBlocked(Double amount) {
		
		boolean result = false;
		
		if(getBalance() >= amount && amount > 0 && getStatus() == AccountStatusType.OPEN) {

			setBalance(getBalance() - amount);
			setBlocked(getBlocked() + amount); 
			result=true;
		}
			
		return result;
	}
	
	
	

	@Override
	public boolean withdrawRealized(Double blockedAmount, Double realizedAmount) {
		boolean result = false;
		
		if((getBlocked() - blockedAmount >= 0) && getStatus() == AccountStatusType.OPEN) {
			setBlocked(getBlocked() - blockedAmount); 
			setBalance(getBalance() + blockedAmount);
			setBalance(getBalance() - realizedAmount);
			
			result=true;
		}
			
		return result;
	}

	@Override
	public boolean deposit(Double amount) {
		
		boolean result = false;

		if(amount > 0 && getStatus() == AccountStatusType.OPEN)
		{
			setBalance(getBalance() + amount);
			// setBlocked(getBlocked() + amount); 
			result=true;
		}
		
		return result;
	}

	
	
	
	@Override
	public boolean releaseBlock(Double amount) {
		boolean result = false;

		if (getBlocked() >= amount && amount > 0 && getStatus() == AccountStatusType.OPEN) {
			
			setBlocked(getBlocked()-amount);
			setBalance(getBalance()+amount);
			result = true;
		}
		
		return result;
	}

	private void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	@Override
	public boolean isTradable() {
		return tradable && getStatus() == AccountStatusType.OPEN;
	}

	@Override
	public AccountStatusType getStatus() {
		return status;
	}

}
