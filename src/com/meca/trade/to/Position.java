package com.meca.trade.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Position extends MecaObject implements IPosition {

	
	private Integer positionNo;
	private TradeStatusType status;
	private Double openLotCount;
	private List<Trade> tradeList;
	private MarketType marketType;
	private TradeType tradeType;
	private PriceData currentPrice;
	private Double entryPrice;
	private Double stopLoss;
	private Double takeProfit;
	private Double profitLoss;
	private Date entryDate;
	private Date exitDate;
	
	
	
	
	public List<Trade> getTradeList() {
		return tradeList;
	}


	public void setTradeList(List<Trade> tradeList) {
		this.tradeList = tradeList;
	}


	public MarketType getMarketType() {
		return marketType;
	}


	public void setMarketType(MarketType marketType) {
		this.marketType = marketType;
	}


	public TradeType getTradeType() {
		return tradeType;
	}


	public void setTradeType(TradeType tradeType) {
		this.tradeType = tradeType;
	}


	public PriceData getCurrentPrice() {
		return currentPrice;
	}


	public void setCurrentPrice(PriceData currentPrice) {
		this.currentPrice = currentPrice;
	}


	public Double getStopLoss() {
		return stopLoss;
	}


	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}


	public Double getTakeProfit() {
		return takeProfit;
	}


	public void setTakeProfit(Double takeProfit) {
		this.takeProfit = takeProfit;
	}


	public Double getProfitLoss() {
		return profitLoss;
	}


	public void setProfitLoss(Double profitLoss) {
		this.profitLoss = profitLoss;
	}


	public void setStatus(TradeStatusType status) {
		this.status = status;
	}


	public void setOpenLotCount(Double openLotCount) {
		this.openLotCount = openLotCount;
	}


	public void setEntryPrice(Double entryPrice) {
		this.entryPrice = entryPrice;
	}


	@Override
	public void updatePriceData(PriceData data) {
		this.currentPrice = data;
		
		if(status == TradeStatusType.OPEN){
			setProfitLoss(((getTradeType() == TradeType.BUY) ?  (getCurrentPrice().getAskPrice() - entryPrice) : (entryPrice - getCurrentPrice().getBidPrice()))
					* getOpenLotCount() 
					* getMarketType().getLotSize());
		}

	}

	
	public TradeStatusType getStatus() {
		return status;
	}
	
	public Integer getPositionNo() {
		return positionNo;
	}

	public void setPositionNo(Integer positionNo) {
		this.positionNo = positionNo;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("positionNo=");
		builder.append(positionNo);
		builder.append(" ");
		builder.append("status=");
		builder.append(status);
		builder.append(" ");
		builder.append("openLotCount=");
		builder.append(openLotCount);
		builder.append("\r\nTrades\r\n");
		
		for(Trade tr : tradeList){
			builder.append("\t\ttrade=");
			builder.append(tr.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}
	
	public Position(Integer positionNo, TradeStatusType status,
			List<Trade> tradeList) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = tradeList;
		this.openLotCount = 0d;
	}
	
	public Position(Integer positionNo, TradeStatusType status) {
		super();
		this.positionNo = positionNo;
		this.status = status;
		this.tradeList = new ArrayList<Trade>();
		this.openLotCount = 0d;
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


	public Double getOpenLotCount() {
		return openLotCount;
	}

	@Override
	public Trade addTrade(Trade trade) {

		if(trade.getPositionNo()==null){
			trade.setPositionNo(positionNo);
		}
		
		if(trade.getTradeNo()==null){
			trade.setTradeNo(tradeList.size());
			tradeList.add(trade);
		}else{
			tradeList.set(trade.getTradeNo(), trade);
		}
		
		if (trade.getTradeType() == TradeType.BUY || trade.getTradeType() == TradeType.SELL) {
			marketType = trade.getMarketType();
			tradeType = trade.getTradeType();
			
			if(trade.getStatus() == TradeStatusType.CLOSE){
				openLotCount = trade.getLot();
				entryPrice = trade.getRealizedPrice();
				stopLoss = trade.getStopLoss();
				takeProfit = trade.getTakeProfit();
				status = TradeStatusType.OPEN;
				entryDate = trade.getRealizedDate();
				
			}
			else if(trade.getStatus()== TradeStatusType.CANCEL){
				if (tradeList.size() == 1) { 
					this.status = TradeStatusType.CANCEL;
				}
			}
		}
		else if ((trade.getTradeType() == TradeType.LEXIT || trade.getTradeType() == TradeType.SEXIT) && trade.getStatus()==TradeStatusType.CLOSE){
			
			openLotCount -= trade.getLot();
			
			if (openLotCount == 0d){
				status = TradeStatusType.CLOSE;
				exitDate = trade.getRealizedDate();
			}
			
			
		}

		trade.updateProfitLoss();
		
		return trade;
	}
	

	
	public Double getCurrentRisk() { 
		return null;
	}


	public Double getEntryPrice(){
		
		return entryPrice;
	}
	

	@Override
	public Double getOpenPL() {
		Double result = 0d;
		result = openLotCount * marketType.getLotSize() * ((tradeType == TradeType.BUY) ?  (currentPrice.getBidPrice() - getEntryPrice()) : (getEntryPrice() - currentPrice.getAskPrice()));
				
		return TradeUtils.getRoundedUpValue(result);
	}

	@Override
	public Double getRealizedProfitLoss(Date startDate, Date endDate) {
		Double result = 0d;
		
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				result += tr.getProfitLoss();
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getRealizedDate().compareTo(endDate) < 0){
							result += tr.getProfitLoss();
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public Double getRealizedGrossProfit(Date startDate, Date endDate) {
		Double result = 0d;
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(tr.getProfitLoss() > 0)
					result += tr.getProfitLoss();
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(endDate) < 0){
							result += tr.getProfitLoss();
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
					}
				}
			}
		}
		
		
		return result;
	}
	
	@Override
	public Integer getTotalNumberOfEntryTrades(Date startDate, Date endDate) {
		Integer result = 0;
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && (tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL) && startDate==null && endDate==null) {
				result += 1;
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE && (tr.getTradeType() == TradeType.BUY || tr.getTradeType() == TradeType.SELL)){
					if(startDate!=null && endDate==null){
						if(tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getRealizedDate().compareTo(endDate) < 0){
							result += 1;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfTrades(Date startDate, Date endDate) {
		Integer result = 0;
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				result += 1;
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getRealizedDate().compareTo(endDate) < 0){
							result += 1;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public Double getLargestWinningTrade(Date startDate, Date endDate){
		Double result = 0d;
		
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(result < tr.getProfitLoss())
					result = tr.getProfitLoss();
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(result < tr.getProfitLoss() && tr.getRealizedDate().compareTo(startDate) >= 0){
							result = tr.getProfitLoss();
						}
						
					} else if(endDate!=null && startDate==null){
						if(result < tr.getProfitLoss() && tr.getRealizedDate().compareTo(endDate) < 0){
							result = tr.getProfitLoss();
						}
						
					} else if(startDate!=null && endDate!=null){
						if(result < tr.getProfitLoss() && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result = tr.getProfitLoss();
						}
					}
				}
			}
		}
		
		
		
		
		return result;
		
	}
	
	public Double getLargestLosingTrade(Date startDate, Date endDate){
		Double result = 0d;
		
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(result > tr.getProfitLoss())
					result = tr.getProfitLoss();
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(result > tr.getProfitLoss() && tr.getRealizedDate().compareTo(startDate) >= 0){
							result = tr.getProfitLoss();
						}
						
					} else if(endDate!=null && startDate==null){
						if(result > tr.getProfitLoss() && tr.getRealizedDate().compareTo(endDate) < 0){
							result = tr.getProfitLoss();
						}
						
					} else if(startDate!=null && endDate!=null){
						if(result > tr.getProfitLoss() && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result = tr.getProfitLoss();
						}
					}
				}
			}
		}
		
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfWinningTrades(Date startDate, Date endDate) {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(tr.getProfitLoss() > 0)
					result += 1;
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(endDate) < 0){
							result += 1;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getProfitLoss() > 0 && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public Integer getTotalNumberOfLosingTrades(Date startDate, Date endDate) {
		Integer result = 0;
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(tr.getProfitLoss() < 0)
					result += 1;
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(endDate) < 0){
							result += 1;
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += 1;
						}
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public Double getRealizedGrossLoss(Date startDate, Date endDate) {
		Double result = 0d;
		
		for (Trade tr : tradeList) {
			if (tr.getStatus() == TradeStatusType.CLOSE && startDate==null && endDate==null) {
				if(tr.getProfitLoss() < 0)
					result += tr.getProfitLoss();
			}else{
				if (tr.getStatus() == TradeStatusType.CLOSE){
					if(startDate!=null && endDate==null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
						
					} else if(endDate!=null && startDate==null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(endDate) < 0){
							result += tr.getProfitLoss();
						}
						
					} else if(startDate!=null && endDate!=null){
						if(tr.getProfitLoss() < 0 && tr.getRealizedDate().compareTo(endDate) < 0 && tr.getRealizedDate().compareTo(startDate) >= 0){
							result += tr.getProfitLoss();
						}
					}
				}
			}
		}
	
		return result;
	}
	
}
