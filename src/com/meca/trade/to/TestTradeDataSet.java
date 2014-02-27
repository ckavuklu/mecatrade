package com.meca.trade.to;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class TestTradeDataSet extends MecaObject implements ITestTradeDataSet {

	private BufferedReader br = null;

	public TestTradeDataSet(String fileName) {
		super();

		try {
			br = new BufferedReader(new FileReader(fileName.trim()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Trade getNext() {

		String line = null;
		Trade tradeData = null;

		try {
			if ((line = br.readLine()) != null) {

				String[] trade = line.split(",");

				tradeData = new Trade();
				tradeData.setEntryDate(new Date());
				tradeData.setStatus(TradeStatusType.OPEN);

				if (StringUtils.isNotEmpty(trade[0])
						&& StringUtils.isNumeric(trade[0]))
					tradeData.setPositionNo(Integer.valueOf(trade[0].trim()));

				if (StringUtils.isNotEmpty(trade[1])
						&& StringUtils.isNumeric(trade[1]))
					tradeData.setTradeNo(Integer.valueOf(trade[1].trim()));

				if (StringUtils.isNotEmpty(trade[2]))
					tradeData.setTradeType(TradeType.valueOf(trade[2].trim()));

				if (StringUtils.isNotEmpty(trade[3]))
					tradeData.setSignal(SignalType.valueOf(trade[3].trim()));

				if (StringUtils.isNotEmpty(trade[4])
						&& NumberUtils.isNumber(trade[4]))
					tradeData.setLot(Double.valueOf(trade[4].trim()));

				if (StringUtils.isNotEmpty(trade[5])
						&& NumberUtils.isNumber(trade[5])){
					
					tradeData.setEntryPrice(Double.valueOf(trade[5].trim()));
					
					if(!(tradeData.getTradeType() == TradeType.BUY || tradeData.getTradeType() == TradeType.SELL) 
							&& StringUtils.isNotEmpty(trade[6])
							&& NumberUtils.isNumber(trade[6])){
						
						tradeData.setExitPrice(Double.valueOf(trade[6].trim()));
						
					}
				}
				if (StringUtils.isNotEmpty(trade[7]))
					tradeData
							.setMarketType(MarketType.valueOf(trade[7].trim()));

			}

		} catch (IOException e) {
			try {
				br.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		return tradeData;
	}

}
