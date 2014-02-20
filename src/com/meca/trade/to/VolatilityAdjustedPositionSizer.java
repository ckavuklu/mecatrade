package com.meca.trade.to;

public class VolatilityAdjustedPositionSizer implements IPositionSizer {
	Double riskPercentage;
	ITrader trader;

	public VolatilityAdjustedPositionSizer(Double value, ITrader trader) {
		this.riskPercentage = value;
		this.trader = trader;
	}

	@Override
	public Double getLotSize(TradeType tradeType) {

		Double equityBasedRisk = trader.getPositionManager().getEquity()
				* riskPercentage;

		Double freeMargin = trader.getPositionManager().getFreeMargin();

		Double risk = (equityBasedRisk < freeMargin) ? equityBasedRisk
				: freeMargin;

		Double lotSizeAccordingToFreeMargin = TradeUtils.roundDownDigits(
				((risk * trader.getPositionManager().getMarketType().getLeverage()) / (tradeType == TradeType.BUY ? trader.getAskPrice() : trader.getBidPrice()))
						/ trader.getPositionManager().getMarketType().getLotSize(), Constants.MARKET_LOT_PRECISION);

		return lotSizeAccordingToFreeMargin;
	}

}
