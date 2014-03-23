package com.meca.trade.to;

import java.util.Date;


public class PerformanceKPIS {
	
	Double netProfitForClosedTrades = 0d;
	Double grossProfitForClosedTrades = 0d;
	Double grossLossForClosedTrades = 0d;
	Double wfe = 0d;
	Integer totalNumberOfTrades = 0;
	Integer totalNumberOfWinningTrades = 0;
	Integer totalNumberOfLosingTrades = 0;
	Integer totalNumberOfEntryTrades = 0;
	Integer totalNumberOfOpenPositions = 0;
	Double largestWinningTrade = 0d;
	Double largestLosingTrade = 0d;
	Double percentProfitable = 0d;
	Double averageWinningTrade = 0d;
	Double averageLosingTrade = 0d;
	Double ratioAverageWinAverageLoss = 0d;
	Double averageTrade = 0d;
	Integer maxConsecutiveWinners = 0;
	Integer maxConsecutiveLosers = 0;
	Double prom = 0d;
	Double annTotalNumberOfWinningTrades = 0d;
	Double annTotalNumberOfLosingTrades = 0d;
	Double annualizedGrossProfit = 0d;
	Double annualizedGrossLoss = 0d;
	Double mdd = 0d;
	Double mddValue = 0d;
	Double annRRR = 0d;
	
	Double strategyStopLimit = 0d;
	Date periodStartDate = null;
	Date periodEndDate = null;
	
	Date mddMaxDate = null;
	Date mddMinDate = null;
	Double annualizedNetProfit = 0d;
	
	public String getHeaders() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("periodStartDate");
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append("periodEndDate");
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append("netProfitForClosedTrades");
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append("grossProfitForClosedTrades");
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("grossLossForClosedTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("totalNumberOfTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("totalNumberOfWinningTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("totalNumberOfLosingTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("totalNumberOfEntryTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("totalNumberOfOpenPositions");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("percentProfitable");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("largestWinningTrade");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("largestLosingTrade");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("averageWinningTrade");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("averageLosingTrade");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("ratioAverageWinAverageLoss");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("averageTrade");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("maxConsecutiveWinners");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("maxConsecutiveLosers");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("PROM");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annTotalNumberOfWinningTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annTotalNumberOfLosingTrades");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annualizedGrossProfit");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annualizedGrossLoss");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annualizedNetProfit");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("MDD(%)");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("MDD");

		builder.append(Constants.CSV_SEPARATOR);
		builder.append("strategyStopLimit(%)");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("mddMaxDate");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("mddMinDate");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("WFE");
		
		builder.append(Constants.CSV_SEPARATOR);
		builder.append("annRRR");

		
		return builder.toString();
	}


	public Date getPeriodStartDate() {
		return periodStartDate;
	}


	public void setPeriodStartDate(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}


	public Date getPeriodEndDate() {
		return periodEndDate;
	}


	public void setPeriodEndDate(Date periodEndDate) {
		this.periodEndDate = periodEndDate;
	}


	public Date getMddMaxDate() {
		return mddMaxDate;
	}


	public void setMddMaxDate(Date mddMaxDate) {
		this.mddMaxDate = mddMaxDate;
	}


	public Date getMddMinDate() {
		return mddMinDate;
	}


	public void setMddMinDate(Date mddMinDate) {
		this.mddMinDate = mddMinDate;
	}
	
	public Double getWfe() {
		return wfe;
	}


	public void setWfe(Double wfe) {
		this.wfe = wfe;
	}
	
	
	public String getPerformanceData() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("periodStartDate=");
		builder.append(TradeUtils.convertToString(periodStartDate));
		builder.append(Constants.FORMAT);
		
		builder.append("periodEndDate=");
		builder.append(TradeUtils.convertToString(periodEndDate));
		builder.append(Constants.FORMAT);
		
		builder.append("netProfitForClosedTrades=");
		builder.append(netProfitForClosedTrades);
		builder.append(Constants.FORMAT);

		builder.append("grossProfitForClosedTrades=");
		builder.append(grossProfitForClosedTrades);
		builder.append(Constants.FORMAT);

		builder.append("grossLossForClosedTrades=");
		builder.append(grossLossForClosedTrades);
		builder.append(Constants.FORMAT);

		builder.append("totalNumberOfTrades=");
		builder.append(totalNumberOfTrades);
		builder.append(Constants.FORMAT);

		builder.append("totalNumberOfWinningTrades=");
		builder.append(totalNumberOfWinningTrades);
		builder.append(Constants.FORMAT);
	
		builder.append("totalNumberOfLosingTrades=");
		builder.append(totalNumberOfLosingTrades);
		builder.append(Constants.FORMAT);

		builder.append("totalNumberOfEntryTrades=");
		builder.append(totalNumberOfEntryTrades);
		builder.append(Constants.FORMAT);
		
		builder.append("totalNumberOfOpenPositions=");
		builder.append(totalNumberOfOpenPositions);
		builder.append(Constants.FORMAT);
		
		builder.append("percentProfitable=");
		builder.append(percentProfitable);
		builder.append(Constants.FORMAT);

		builder.append("largestWinningTrade=");
		builder.append(largestWinningTrade);
		builder.append(Constants.FORMAT);
		
		builder.append("largestLosingTrade=");
		builder.append(largestLosingTrade);
		builder.append(Constants.FORMAT);

		builder.append("averageWinningTrade=");
		builder.append(averageWinningTrade);
		builder.append(Constants.FORMAT);
	
		builder.append("averageLosingTrade=");
		builder.append(averageLosingTrade);
		builder.append(Constants.FORMAT);

		builder.append("ratioAverageWinAverageLoss=");
		builder.append(ratioAverageWinAverageLoss);
		builder.append(Constants.FORMAT);

		builder.append("averageTrade=");
		builder.append(averageTrade);
		builder.append(Constants.FORMAT);
	
		builder.append("maxConsecutiveWinners=");
		builder.append(maxConsecutiveWinners);
		builder.append(Constants.FORMAT);
	
		builder.append("maxConsecutiveLosers=");
		builder.append(maxConsecutiveLosers);
		builder.append(Constants.FORMAT);
		
		builder.append("prom=");
		builder.append(prom);
		builder.append(Constants.FORMAT);
		
		builder.append("annTotalNumberOfWinningTrades=");
		builder.append(annTotalNumberOfWinningTrades);
		builder.append(Constants.FORMAT);
		
		builder.append("annTotalNumberOfLosingTrades=");
		builder.append(annTotalNumberOfLosingTrades);
		builder.append(Constants.FORMAT);
		
		builder.append("annualizedGrossProfit=");
		builder.append(annualizedGrossProfit);
		builder.append(Constants.FORMAT);
		
		builder.append("annualizedGrossLoss=");
		builder.append(annualizedGrossLoss);
		builder.append(Constants.FORMAT);
		
		builder.append("annualizedNetProfit=");
		builder.append(annualizedNetProfit);
		builder.append(Constants.FORMAT);
		
		
		
		builder.append("mdd(%)=");
		builder.append(mdd);
		builder.append(Constants.FORMAT);
		
		builder.append("mddValue=");
		builder.append(mddValue);
		builder.append(Constants.FORMAT);
		
		builder.append("strategyStopLimit=");
		builder.append(strategyStopLimit);
		builder.append(Constants.FORMAT);
		
		builder.append("mddMaxDate=");
		builder.append(TradeUtils.convertToString(mddMaxDate));
		builder.append(Constants.FORMAT);
		
		builder.append("mddMinDate=");
		builder.append(TradeUtils.convertToString(mddMinDate));
		builder.append(Constants.FORMAT);
		
		builder.append("wfe=");
		builder.append(wfe);
		builder.append(Constants.FORMAT);
		
		builder.append("annRRR=");
		builder.append(annRRR);
		
		
		return builder.toString();
	}


	public Double getMddValue() {
		return mddValue;
	}


	public void setMddValue(Double mddValue) {
		this.mddValue = mddValue;
	}


	public Double getAnnRRR() {
		return annRRR;
	}


	public void setAnnRRR(Double annRRR) {
		this.annRRR = annRRR;
	}


	public String getData() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(TradeUtils.convertToString(periodStartDate));
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(TradeUtils.convertToString(periodEndDate));
		builder.append(Constants.CSV_SEPARATOR);
		
		
		builder.append(netProfitForClosedTrades);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(grossProfitForClosedTrades);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(grossLossForClosedTrades);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(totalNumberOfTrades);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(totalNumberOfWinningTrades);
		builder.append(Constants.CSV_SEPARATOR);
	
		builder.append(totalNumberOfLosingTrades);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(totalNumberOfEntryTrades);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(totalNumberOfOpenPositions);
		builder.append(Constants.CSV_SEPARATOR);
	
		builder.append(percentProfitable);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(largestWinningTrade);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(largestLosingTrade);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(averageWinningTrade);
		builder.append(Constants.CSV_SEPARATOR);
	
		builder.append(averageLosingTrade);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(ratioAverageWinAverageLoss);
		builder.append(Constants.CSV_SEPARATOR);

		builder.append(averageTrade);
		builder.append(Constants.CSV_SEPARATOR);
	
		builder.append(maxConsecutiveWinners);
		builder.append(Constants.CSV_SEPARATOR);
	
		builder.append(maxConsecutiveLosers);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(prom);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annTotalNumberOfWinningTrades);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annTotalNumberOfLosingTrades);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annualizedGrossProfit);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annualizedGrossLoss);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annualizedNetProfit);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(mdd);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(mddValue);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(strategyStopLimit);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(TradeUtils.convertToString(mddMaxDate));
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(TradeUtils.convertToString(mddMinDate));
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(wfe);
		builder.append(Constants.CSV_SEPARATOR);
		
		builder.append(annRRR);
		
		
		return builder.toString();
	}
	
	
	public Integer getTotalNumberOfOpenPositions() {
		return totalNumberOfOpenPositions;
	}


	public void setTotalNumberOfOpenPositions(Integer totalNumberOfOpenPositions) {
		this.totalNumberOfOpenPositions = totalNumberOfOpenPositions;
	}


	public PerformanceKPIS(Date periodStartDate, Date periodEndDate) {
		super();
		this.periodStartDate = periodStartDate;
		this.periodEndDate = periodEndDate;
	}
	
	public PerformanceKPIS() {
		super();
	}
	
	public Double getNetProfitForClosedTrades() {
		return netProfitForClosedTrades;
	}
	public void setNetProfitForClosedTrades(Double netProfitForClosedTrades) {
		this.netProfitForClosedTrades = netProfitForClosedTrades;
	}
	public Double getGrossProfitForClosedTrades() {
		return grossProfitForClosedTrades;
	}
	public void setGrossProfitForClosedTrades(Double grossProfitForClosedTrades) {
		this.grossProfitForClosedTrades = grossProfitForClosedTrades;
	}
	public Double getGrossLossForClosedTrades() {
		return grossLossForClosedTrades;
	}
	public void setGrossLossForClosedTrades(Double grossLossForClosedTrades) {
		this.grossLossForClosedTrades = grossLossForClosedTrades;
	}
	public Integer getTotalNumberOfTrades() {
		return totalNumberOfTrades;
	}
	public void setTotalNumberOfTrades(Integer totalNumberOfTrades) {
		this.totalNumberOfTrades = totalNumberOfTrades;
	}
	public Integer getTotalNumberOfWinningTrades() {
		return totalNumberOfWinningTrades;
	}
	public void setTotalNumberOfWinningTrades(Integer totalNumberOfWinningTrades) {
		this.totalNumberOfWinningTrades = totalNumberOfWinningTrades;
	}
	public Integer getTotalNumberOfLosingTrades() {
		return totalNumberOfLosingTrades;
	}
	public void setTotalNumberOfLosingTrades(Integer totalNumberOfLosingTrades) {
		this.totalNumberOfLosingTrades = totalNumberOfLosingTrades;
	}
	public Integer getTotalNumberOfEntryTrades() {
		return totalNumberOfEntryTrades;
	}
	public void setTotalNumberOfEntryTrades(Integer totalNumberOfEntryTrades) {
		this.totalNumberOfEntryTrades = totalNumberOfEntryTrades;
	}
	public Double getLargestWinningTrade() {
		return largestWinningTrade;
	}
	public void setLargestWinningTrade(Double largestWinningTrade) {
		this.largestWinningTrade = largestWinningTrade;
	}
	public Double getLargestLosingTrade() {
		return largestLosingTrade;
	}
	public void setLargestLosingTrade(Double largestLosingTrade) {
		this.largestLosingTrade = largestLosingTrade;
	}
	public Double getPercentProfitable() {
		return percentProfitable;
	}
	public void setPercentProfitable(Double percentProfitable) {
		this.percentProfitable = percentProfitable;
	}
	public Double getAverageWinningTrade() {
		return averageWinningTrade;
	}
	public void setAverageWinningTrade(Double averageWinningTrade) {
		this.averageWinningTrade = averageWinningTrade;
	}
	public Double getAverageLosingTrade() {
		return averageLosingTrade;
	}
	public void setAverageLosingTrade(Double averageLosingTrade) {
		this.averageLosingTrade = averageLosingTrade;
	}
	public Double getRatioAverageWinAverageLoss() {
		return ratioAverageWinAverageLoss;
	}
	public void setRatioAverageWinAverageLoss(Double ratioAverageWinAverageLoss) {
		this.ratioAverageWinAverageLoss = ratioAverageWinAverageLoss;
	}
	public Double getAverageTrade() {
		return averageTrade;
	}
	public void setAverageTrade(Double averageTrade) {
		this.averageTrade = averageTrade;
	}
	public Integer getMaxConsecutiveWinners() {
		return maxConsecutiveWinners;
	}
	public void setMaxConsecutiveWinners(Integer maxConsecutiveWinners) {
		this.maxConsecutiveWinners = maxConsecutiveWinners;
	}
	public Integer getMaxConsecutiveLosers() {
		return maxConsecutiveLosers;
	}
	public void setMaxConsecutiveLosers(Integer maxConsecutiveLosers) {
		this.maxConsecutiveLosers = maxConsecutiveLosers;
	}
	public Double getProm() {
		return prom;
	}
	public void setProm(Double prom) {
		this.prom = prom;
	}
	public Double getAnnTotalNumberOfWinningTrades() {
		return annTotalNumberOfWinningTrades;
	}
	public void setAnnTotalNumberOfWinningTrades(
			Double annTotalNumberOfWinningTrades) {
		this.annTotalNumberOfWinningTrades = annTotalNumberOfWinningTrades;
	}
	public Double getAnnTotalNumberOfLosingTrades() {
		return annTotalNumberOfLosingTrades;
	}
	public void setAnnTotalNumberOfLosingTrades(Double annTotalNumberOfLosingTrades) {
		this.annTotalNumberOfLosingTrades = annTotalNumberOfLosingTrades;
	}
	public Double getAnnualizedGrossProfit() {
		return annualizedGrossProfit;
	}
	public void setAnnualizedGrossProfit(Double annualizedGrossProfit) {
		this.annualizedGrossProfit = annualizedGrossProfit;
	}
	public Double getAnnualizedGrossLoss() {
		return annualizedGrossLoss;
	}
	public void setAnnualizedGrossLoss(Double annualizedGrossLoss) {
		this.annualizedGrossLoss = annualizedGrossLoss;
	}
	public Double getMdd() {
		return mdd;
	}
	public void setMdd(Double mdd) {
		this.mdd = mdd;
	}
	public Double getStrategyStopLimit() {
		return strategyStopLimit;
	}
	public void setStrategyStopLimit(Double strategyStopLimit) {
		this.strategyStopLimit = strategyStopLimit;
	}

	public Double getAnnualizedNetProfit() {
		return annualizedNetProfit;
	}

	public void setAnnualizedNetProfit(Double annualizedNetProfit) {
		this.annualizedNetProfit = annualizedNetProfit;
	}
	
}
