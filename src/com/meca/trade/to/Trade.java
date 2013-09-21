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
	private Long tradeNo;
	private Long positionNo;
	private TradeStatusType status;
	
	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double openPrice, Date openDate, TradeStatusType status) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.openPrice = openPrice;
		this.openDate = openDate;
		this.status = status;
	}
	
	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double openPrice, Date openDate, TradeStatusType status, Long positionNo) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.openPrice = openPrice;
		this.openDate = openDate;
		this.status = status;
		this.positionNo = positionNo;
	}
	
	public Long getPositionNo() {
		return positionNo;
	}


	public void setPositionNo(Long positionNo) {
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
	public Double getPrice() {
		return openPrice;
	}
	public void setPrice(Double price) {
		this.openPrice = price;
	}
	public Long getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(Long tradeNo) {
		this.tradeNo = tradeNo;
	}
}
