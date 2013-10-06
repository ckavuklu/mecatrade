package com.meca.trade.to;

import java.util.Date;

/**
 * This is the realized trade order
 * 
 * 
 * @author ACER
 *
 */
public class Trade extends MecaObject {

	
	private String signalName;
	private TradeType tradeType;
	private SignalType signal;
	private Double lot;
	private Double openPrice;
	private Double realizedPrice;
	private Date openDate;
	private Date realizedDate;
	private Double profitLoss;
	private Integer tradeNo;
	private Integer positionNo;
	private TradeStatusType status;
	private MarketType marketType;
	
	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double openPrice, Date openDate, TradeStatusType status, MarketType type) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.openPrice = openPrice;
		this.openDate = openDate;
		this.status = status;
		this.marketType = type;
	}
	
	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double openPrice, Date openDate, TradeStatusType status, Integer positionNo, MarketType type) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.openPrice = openPrice;
		this.openDate = openDate;
		this.status = status;
		this.positionNo = positionNo;
		this.marketType = type;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("signalName=");
		builder.append(signalName);
		builder.append(" ");
		builder.append("tradeType=");
		builder.append(tradeType);
		builder.append(" ");
		builder.append("signal=");
		builder.append(signal);
		builder.append(" ");
		builder.append("lot=");
		builder.append(lot);
		builder.append(" ");
		builder.append("openPrice=");
		builder.append(openPrice);
		builder.append(" ");
		builder.append("realizedPrice=");
		builder.append(realizedPrice);
		builder.append(" ");
		builder.append("openDate=");
		builder.append(openDate);
		builder.append(" ");
		builder.append("realizedDate=");
		builder.append(realizedDate);
		builder.append(" ");
		builder.append("profitLoss=");
		builder.append(profitLoss);
		builder.append(" ");
		builder.append("tradeNo=");
		builder.append(tradeNo);
		builder.append(" ");
		builder.append("positionNo=");
		builder.append(positionNo);
		builder.append(" ");
		builder.append("type=");
		builder.append(marketType);
		builder.append(" ");
		builder.append("status=");
		builder.append(status);
		builder.append(" ");
		
		return builder.toString();
	}
	
	public Integer getPositionNo() {
		return positionNo;
	}


	public void setPositionNo(Integer positionNo) {
		this.positionNo = positionNo;
	}


	public Double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}
	public Double getRealizedPrice() {
		return realizedPrice;
	}
	public void setRealizedPrice(Double realizedPrice) {
		this.realizedPrice = realizedPrice;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public Date getRealizedDate() {
		return realizedDate;
	}
	public void setRealizedDate(Date realizedDate) {
		this.realizedDate = realizedDate;
	}
	
	public Double getProfitLoss() {
		return profitLoss;
	}
	public void setProfitLoss(Double profitLoss) {
		this.profitLoss = profitLoss;
	}
	public TradeStatusType getStatus() {
		return status;
	}
	public void setStatus(TradeStatusType status) {
		this.status = status;
	}
	
	public Date getDate() {
		return openDate;
	}
	public void setDate(Date date) {
		this.openDate = date;
	}
	public String getSignalName() {
		return signalName;
	}
	public void setSignalName(String signalName) {
		this.signalName = signalName;
	}
	public TradeType getTradeType() {
		return tradeType;
	}
	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}
	public SignalType getSignal() {
		return signal;
	}
	public void setSignal(SignalType signal) {
		this.signal = signal;
	}
	public Double getLot() {
		return lot;
	}
	public void setLot(Double lot) {
		this.lot = lot;
	}
	
	public Integer getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(Integer tradeNo) {
		this.tradeNo = tradeNo;
	}

	public MarketType getMarketType() {
		return marketType;
	}

	public void setMarketType(MarketType type) {
		this.marketType = type;
	}


}
