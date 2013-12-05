package com.meca.trade.to;

import java.util.Date;

public class RunConfiguration {
	
	private Date periodStart = null;
	private Date periodEnd = null;
	private Integer periodStepSize = null;
	private String periodType = null;
	private Double accountBalance = null;
	private String inputMarketDataFile = null;
	private String inputTestTradeDataFile = null;

	public RunConfiguration() {
		
	}

	public Date getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}

	public Integer getPeriodStepSize() {
		return periodStepSize;
	}

	public void setPeriodStepSize(Integer periodStepSize) {
		this.periodStepSize = periodStepSize;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getInputMarketDataFile() {
		return inputMarketDataFile;
	}

	public void setInputMarketDataFile(String inputMarketDataFile) {
		this.inputMarketDataFile = inputMarketDataFile;
	}

	public String getInputTestTradeDataFile() {
		return inputTestTradeDataFile;
	}

	public void setInputTestTradeDataFile(String inputTestTradeDataFile) {
		this.inputTestTradeDataFile = inputTestTradeDataFile;
	}

	
}
