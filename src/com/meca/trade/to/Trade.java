package com.meca.trade.to;

import java.util.Date;

/**
 * This is the realized trade order
 * 
 * 
 * @author ACER
 *
 */
public class Trade extends MecaObject{

	
	private TradeType tradeType;
	private SignalType signal;
	private Double lot;
	private Double entryPrice;
	private Double realizedPrice;
	private Double exitPrice;
	private Date entryDate;
	private Date exitDate;
	private Date realizedDate;
	private Double profitLoss;
	private Integer tradeNo;
	private Integer positionNo;
	private TradeStatusType status;
	private MarketType marketType;
	private Double takeProfit;
	private Double stopLoss;
	
	
	public Double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(Double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public Double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(Double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getExitDate() {
		return exitDate;
	}

	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}

	public Double getTakeProfit() {
		return takeProfit;
	}

	public void setTakeProfit(Double takeProfit) {
		this.takeProfit = takeProfit;
	}

	public Double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}

	
	public Trade(Integer positionNo, Integer tradeNo, TradeType tradeType, SignalType signal, Double lot,
			Double openPrice, Date openDate, TradeStatusType status, MarketType type) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.entryPrice = openPrice;
		this.entryDate = openDate;
		this.status = status;
		this.marketType = type;
		this.profitLoss = 0d;
		this.tradeNo = tradeNo;
		this.positionNo = positionNo;
	}
	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double entryPrice, Date openDate, TradeStatusType status, MarketType type) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.entryPrice = entryPrice;
		this.entryDate = openDate;
		this.status = status;
		this.marketType = type;
		this.profitLoss = 0d;
	}
	

	public Trade() {
		super();
		profitLoss = 0d;
	}

	
	public Trade(TradeType tradeType, SignalType signal, Double lot,
			Double entryPrice, Date openDate, TradeStatusType status, Integer positionNo, MarketType type) {
		super();
		
		this.tradeType = tradeType;
		this.signal = signal;
		this.lot = lot;
		this.entryPrice = entryPrice;
		this.entryDate = openDate;
		this.status = status;
		this.positionNo = positionNo;
		this.marketType = type;
		this.profitLoss = 0d;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("tradeType=");
		builder.append(tradeType);
		builder.append(" ");
		builder.append("signal=");
		builder.append(signal);
		builder.append(" ");
		builder.append("lot=");
		builder.append(lot);
		builder.append(" ");
		builder.append("entryPrice=");
		builder.append(entryPrice);
		builder.append(" ");
		builder.append("exitPrice=");
		builder.append(exitPrice);
		builder.append(" ");
		builder.append("realizedPrice=");
		builder.append(realizedPrice);
		builder.append(" ");
		builder.append("entryDate=");
		builder.append(entryDate);
		builder.append(" ");
		builder.append("realizedDate=");
		builder.append(realizedDate);
		builder.append(" ");
		builder.append("profitLoss=");
		builder.append(profitLoss);
		builder.append(" ");
		builder.append("stopLoss=");
		builder.append(stopLoss);
		builder.append(" ");
		builder.append("takeProfit=");
		builder.append(takeProfit);
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


	
	public Double getRealizedPrice() {
		return realizedPrice;
	}
	public void setRealizedPrice(Double realizedPrice) {
		this.realizedPrice = realizedPrice;
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
		return entryDate;
	}
	public void setDate(Date date) {
		this.entryDate = date;
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
	
	
	public void updateProfitLoss() {
		
		if(getStatus()==TradeStatusType.CLOSE){
			
			if((getTradeType() == TradeType.LEXIT || getTradeType() == TradeType.SEXIT))
			{
				setProfitLoss(((getTradeType() == TradeType.LEXIT) ?  (getRealizedPrice() - entryPrice) : (entryPrice - getRealizedPrice()))
					* getLot() 
					* getMarketType().getLotSize());
			}else{
				setProfitLoss(0d);
			}
			
		}
	
	}

}
