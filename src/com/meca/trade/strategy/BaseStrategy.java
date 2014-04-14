package com.meca.trade.strategy;

import java.util.ArrayList;
import java.util.List;

import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants;
import com.meca.trade.to.IStrategy;
import com.meca.trade.to.IndicatorSet;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;
import com.meca.trade.to.TradeUtils;

public class BaseStrategy implements IStrategy {
	Packet[] pArray;
	PriceData data;
	List<String> indexArray;

	IndicatorSet set;

	public BaseStrategy(IndicatorSet set) {
		super();
		this.set = set;
	}

	public BaseStrategy() {
		super();
		set = new IndicatorSet();
	}

	@Override
	public void addIndicator(String indicatorName, Integer indicatorPort) {
		set.addIndicator(indicatorName, indicatorPort);
	}

	@Override
	public StrategyDecision execute(Packet[] pArray, PriceData price) {
		this.pArray = pArray;
		this.data = price;
		
		return null;
	}

	public String getIndicatorHeaders() {
		StringBuilder builder = new StringBuilder();
		if (set != null && set.getMap().size() > 0) {
			indexArray = new ArrayList<String>(set.getMap().keySet());

			for (int i = 0; i < indexArray.size() - 1; i++) {
				builder.append(Constants.GRAPH_DATA_JSON_ESCAPE_STRING);
				builder.append(indexArray.get(i));
				builder.append(Constants.GRAPH_DATA_JSON_ESCAPE_STRING);
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			}

			builder.append(Constants.GRAPH_DATA_JSON_ESCAPE_STRING);
			builder.append(indexArray.get(indexArray.size() - 1));
			builder.append(Constants.GRAPH_DATA_JSON_ESCAPE_STRING);
		}

		return builder.toString();

	}

	public String getIndicatorData() {

		StringBuilder builder = new StringBuilder();

		if (set != null && set.getMap().size() > 0) {
			Double indicatorValue = Double.NaN;

			for (int i = 0; i < indexArray.size() - 1; i++) {
				indicatorValue = (Double) (pArray[set.getMap().get(indexArray.get(i))])
						.getContent();
				builder.append(TradeUtils.roundUpDigits(indicatorValue.equals(Double.NaN)?0d:indicatorValue, data.getMarketType().getPricePrecision()));
				builder.append(Constants.GRAPH_DATA_JSON_SEPARATOR_STRING);
			}

			indicatorValue = (Double) (pArray[set.getMap().get(
					indexArray.get(indexArray.size() - 1))]).getContent();
			builder.append(TradeUtils.roundUpDigits(indicatorValue.equals(Double.NaN)?0d:indicatorValue,data.getMarketType().getPricePrecision()));
		}

		return builder.toString();
	}

}