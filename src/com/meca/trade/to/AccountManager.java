package com.meca.trade.to;

import java.util.List;

public class AccountManager extends MecaObject implements IAccountManager{

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(IAccount acc:accountList){
			builder.append("\t\taccount=");
			builder.append(acc.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}

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
		
		return balance;
	}

	@Override
	public boolean withdraw(Trade trade) {
		boolean result=false;
		CurrencyType currency=null;
		Double blockAmount = null;
		
		
		if(trade.getTradeType() == TradeType.BUY){
			 currency = trade.getMarketType().getQuoteCurrency();
			 blockAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getOpenPrice();
			 
		}
		
		else if(trade.getTradeType() == TradeType.SELL){
			 currency = trade.getMarketType().getBaseCurrency();
			 blockAmount = trade.getLot() * trade.getMarketType().getLotSize();
		}
			
		
		IAccount account = getAccount(currency);
		
		
		if(account!=null){
			if (trade.getStatus() == TradeStatusType.OPEN){
	
				result = account.withdrawBlocked(blockAmount);
			}
	
			else if (trade.getStatus() == TradeStatusType.CLOSE){
			
				Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * (trade.getTradeType() == TradeType.BUY?trade.getRealizedPrice():1d);
				result = account.withdrawRealized(blockAmount, realAmount);
	
			}
		}
		
		
		return result;
	}

	@Override
	public boolean deposit(Trade trade) {
		
		boolean result=false;
		
		IAccount baseAccount = getAccount(trade.getMarketType().getBaseCurrency());
		IAccount quoteAccount = getAccount(trade.getMarketType().getQuoteCurrency());
		
		
		if(baseAccount!=null && quoteAccount!=null){
			
			if (trade.getStatus() == TradeStatusType.CLOSE){
				
				
				
				if(trade.getTradeType() == TradeType.SEXIT){
				
					Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * 1d;
					result = baseAccount.deposit(realAmount);
					quoteAccount.deposit(trade.getProfitLoss());
				}else{
					Double realAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getRealizedPrice();
					result = quoteAccount.deposit(realAmount);
					//quoteAccount.deposit(trade.getProfitLoss());
				}
	
			}
		}
		
		
		return result;
	}

	
	
	@Override
	public boolean cancel(Trade trade) {
		
		boolean result=false;
		CurrencyType currency=null;
		
		
		if(trade.getTradeType() == TradeType.BUY)
			 currency = trade.getMarketType().getQuoteCurrency();
		
		else if(trade.getTradeType() == TradeType.SELL)
			 currency = trade.getMarketType().getBaseCurrency();
			
		Double blockAmount = trade.getLot() * trade.getMarketType().getLotSize() * trade.getOpenPrice();
		IAccount account = getAccount(currency);
		
		
		if(account!=null){
	
			result = account.releaseBlock(blockAmount);
		
		}
		
		return false;
	}

	public IAccount getAccount(CurrencyType currency){

		// Account Manager assumes one account for each currency
		
		IAccount result = null;
		
		for(IAccount account:accountList){
			
			if(account.getCurrency()==currency && account.getStatus() == AccountStatusType.OPEN){
				result = account; break;
				
			}
		}
		
		
		return result;
		
		
	}
	
	
}
