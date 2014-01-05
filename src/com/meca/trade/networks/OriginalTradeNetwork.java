package com.meca.trade.networks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import com.jpmorrsn.fbp.engine.Network;
import com.meca.trade.to.Account;
import com.meca.trade.to.AccountStatusType;
import com.meca.trade.to.BaseTrader;
import com.meca.trade.to.CurrencyType;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.IndicatorSet;
import com.meca.trade.to.MarketType;
import com.meca.trade.to.PerformanceReportManager;
import com.meca.trade.to.PositionManager;
import com.meca.trade.to.RunConfiguration;
import com.meca.trade.to.SMAandEMAStrategy;
import com.meca.trade.to.StochasticStrategy;
import com.meca.trade.to.TestTradeDataSet;
import com.meca.trade.to.TradeUtils;

public class OriginalTradeNetwork extends Network {

	@Override
	  protected void define() {
	    //component("_Discard", com.jpmorrsn.fbp.components.Discard.class);
	    //component("_Write_text_to_pane", com.jpmorrsn.fbp.components.ShowText.class);
		
		Properties prop = new Properties();
		RunConfiguration config = new RunConfiguration();

        try {
               prop.load(new FileInputStream("config.properties"));
               
        } catch (IOException ex) {

               ex.printStackTrace();
        }
        
        config.setPeriodStart(TradeUtils.getTime(prop.getProperty("start_of_trading_period")));
        config.setPeriodEnd(TradeUtils.getTime(prop.getProperty("end_of_trading_period")));
        config.setPeriodStepSize(Integer.valueOf(prop.getProperty("trading_period_step_size")));
        config.setPeriodType(prop.getProperty("trading_period_type"));
        config.setAccountBalance(Double.valueOf(prop.getProperty("usd_account_balance")));
		
        config.setInputMarketDataFile(prop.getProperty("input_market_data_file_name"));
        config.setInputTestTradeDataFile(prop.getProperty("input_test_trade_data_file_name"));
		
        HashMap<String,Parameter> map = new HashMap<String,Parameter>();
        map.put("PERIOD_START", new Parameter("PERIOD_START","Date",prop.getProperty("start_of_trading_period")));
        map.put("PERIOD_END", new Parameter("PERIOD_END","Date",prop.getProperty("end_of_trading_period")));
        map.put("ACCOUNT_BALANCE", new Parameter("ACCOUNT_BALANCE","Double",prop.getProperty("usd_account_balance")));
        map.put("INPUT_MARKET_DATA_FILE_NAME", new Parameter("INPUT_MARKET_DATA_FILE_NAME","String",prop.getProperty("input_market_data_file_name")));
        map.put("INPUT_TEST_TRADE_DATA_FILE_NAME", new Parameter("INPUT_TEST_TRADE_DATA_FILE_NAME","String",prop.getProperty("input_test_trade_data_file_name")));
        
  
        //stochastic_overbought_level
		PerformanceReportManager reportManager = new PerformanceReportManager(map);
		
		Account usdAcc = new Account(CurrencyType.USD,"5678",config.getAccountBalance(),AccountStatusType.OPEN);
		
		
		SMAandEMAStrategy smaStrategy = new SMAandEMAStrategy();
		smaStrategy.addIndicator("EMASHORT", 5);
		smaStrategy.addIndicator("EMALONG", 6);
		//SMAStrategy smaStrategy = new SMAStrategy();
		smaStrategy.addIndicator("SMASHORT", 4);
		smaStrategy.addIndicator("SMALONG", 9);
		
		
		
		
		
		IndicatorSet stochasticKLine = new IndicatorSet();
		stochasticKLine.addIndicator("KLINE", 10);
		IndicatorSet stochasticDLine = new IndicatorSet();
		stochasticDLine.addIndicator("DLINE", 11);
		StochasticStrategy stochasticStrategy = new StochasticStrategy(stochasticKLine,stochasticDLine,80d,20d);
		
			
		IPositionManager posManager = new PositionManager(null,usdAcc,reportManager,MarketType.EURUSD);
		TestTradeDataSet dataSet = new TestTradeDataSet(config.getInputTestTradeDataFile());
		//TurtleStrategy turtleStrategy = new TurtleStrategy(shortSet);
		
		
		BaseTrader base = new BaseTrader(posManager);
		
	    
		// Trade Data Components
	    component("_DataFeeder", com.meca.trade.components.DataFeeder.class);
	    
	    component("_QuotePrice_C", com.meca.trade.components.QuotePrice.class);
	    /*component("_QuotePrice_O", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_H", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_L", com.meca.trade.components.QuotePrice.class);*/
	    
	    component("_Kicker", com.meca.trade.components.Kicker.class);
	    
	    // Trade Data Initializations
	    
	    initialize("C", component("_QuotePrice_C"), port("PRICETYPE"));
/*	    initialize("H", component("_QuotePrice_H"), port("PRICETYPE"));
	    initialize("L", component("_QuotePrice_L"), port("PRICETYPE"));
	    initialize("O", component("_QuotePrice_O"), port("PRICETYPE"));
	   */
	    //initialize("ALL", component("_DataFeeder"), port("SCHEDULETYPE"));
	    initialize(config.getPeriodType(), component("_DataFeeder"), port("SCHEDULETYPE"));
	    initialize(config.getPeriodStepSize(), component("_DataFeeder"), port("SCHEDULEPERIOD"));
	    
	    
	    
	    initialize(config.getPeriodStart(), component("_DataFeeder"), port("PERIODSTART"));
	    initialize(config.getPeriodEnd(), component("_DataFeeder"), port("PERIODEND"));
	    
	    
	    initialize(config.getInputMarketDataFile(), component("_DataFeeder"), port("FILENAME"));

	    
	    // Indicator Components
	    component("_SimpleMovingAverage_SHORT", com.meca.trade.components.SimpleMovingAverage.class);
	    component("_SimpleMovingAverage_LONG", com.meca.trade.components.SimpleMovingAverage.class);
	    
	    component("_ExponentialMovingAverage_SHORT", com.meca.trade.components.ExponentialMovingAverage.class);
	    component("_ExponentialMovingAverage_LONG", com.meca.trade.components.ExponentialMovingAverage.class);
	    /*component("_MACD", com.meca.trade.networks.subnets.MACDSubNetwork.class);*/
	    
	    
	    
	    initialize(Double.valueOf(5), component("_ExponentialMovingAverage_SHORT"), port("WINDOW"));
	    initialize(Double.valueOf(12), component("_ExponentialMovingAverage_LONG"), port("WINDOW"));
/*	    initialize(Double.valueOf(12), component("_MACD"), port("SHORTEMAPERIOD"));
	    initialize(Double.valueOf(26), component("_MACD"), port("LONGEMAPERIOD"));
	    initialize(Double.valueOf(9), component("_MACD"), port("SIGNALPERIOD"));*/
	    
	    
	    
	    // Stragety
	    component("_TradeMultiplexer", com.meca.trade.components.TradeMultiplexer.class);
	    component("_PortolioManager", com.meca.trade.components.PortolioManager.class);
	    component("_ActionManager", com.meca.trade.components.ActionManager.class);
	
	    
	    initialize(posManager, component("_PortolioManager"), port("MANAGER"));
	    initialize(dataSet, component("_PortolioManager"), port("TESTTRADEDATASET"));
	    initialize(base, component("_PortolioManager"), port("TRADER"));
	    initialize(posManager, component("_ActionManager"), port("MANAGER"));
	    
	    //stochasticStrategy
	    initialize(/*stochasticStrategy*/ smaStrategy, component("_TradeMultiplexer"), port("STRATEGY"));
	    

	    
	    //connect(component("_DataSource"), port("OUT"), component("_DataFeeder"), port("TRADEDATA"));
	    
	    connect(component("_DataFeeder"), port("OUT",0), component("_QuotePrice_C"), port("TRADEDATA"));
	    /*connect(component("_DataFeeder"), port("OUT",1), component("_QuotePrice_O"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",2), component("_QuotePrice_H"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",3), component("_QuotePrice_L"), port("TRADEDATA"));*/
	    
	    connect(component("_DataFeeder"), port("OUT",1), component("_TradeMultiplexer"), port("MARKETDATA"));

	    connect(component("_Kicker"), port("OUT",0), component("_DataFeeder"), port("KICKOFF"));
	    /*connect(component("_Kicker"), port("OUT",0), component("_QuotePrice_C"), port("KICKOFF"));
		connect(component("_Kicker"), port("OUT",1), component("_QuotePrice_O"), port("KICKOFF"));
	    connect(component("_Kicker"), port("OUT",2), component("_QuotePrice_H"), port("KICKOFF"));
	    connect(component("_Kicker"), port("OUT",3), component("_QuotePrice_L"), port("KICKOFF"));*/
	   
	    
	    connect(component("_ActionManager"), port("CLOCKTICK",0), component("_DataFeeder"), port("CLOCKTICK"));
	    /*connect(component("_ActionManager"), port("CLOCKTICK",0), component("_QuotePrice_C"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",1), component("_QuotePrice_O"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",2), component("_QuotePrice_H"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",3), component("_QuotePrice_L"), port("CLOCKTICK"));*/
	    
	    connect(component("_QuotePrice_C"), port("OUT",2), component("_SimpleMovingAverage_SHORT"), port("DATA"));
	    connect(component("_QuotePrice_C"), port("OUT",3), component("_SimpleMovingAverage_LONG"), port("DATA"));
	    
	    connect(component("_QuotePrice_C"), port("OUT",4), component("_ExponentialMovingAverage_SHORT"), port("DATA"));
	    connect(component("_QuotePrice_C"), port("OUT",5), component("_ExponentialMovingAverage_LONG"), port("DATA"));
	    
	    
	    /*
	    connect(component("_QuotePrice_O"), port("OUT",0), component("_TradeMultiplexer"), port("IN",0));
	    connect(component("_QuotePrice_C"), port("OUT",0), component("_TradeMultiplexer"), port("IN",1));
	    connect(component("_QuotePrice_H"), port("OUT",0), component("_TradeMultiplexer"), port("IN",2));
	    connect(component("_QuotePrice_L"), port("OUT",0), component("_TradeMultiplexer"), port("IN",3));
	  */
	    connect(component("_SimpleMovingAverage_SHORT"), port("OUT"), component("_TradeMultiplexer"), port("IN",4));
	    connect(component("_ExponentialMovingAverage_SHORT"), port("OUT",0), component("_TradeMultiplexer"), port("IN",5));
	    connect(component("_ExponentialMovingAverage_LONG"), port("OUT",0), component("_TradeMultiplexer"), port("IN",6));
/*	    connect(component("_MACD"), port("MACDLINE"), component("_TradeMultiplexer"), port("IN",6));
	    connect(component("_MACD"), port("SIGNALLINE"), component("_TradeMultiplexer"), port("IN",7));
	    connect(component("_MACD"), port("HISTOGRAM"), component("_TradeMultiplexer"), port("IN",8));*/
	    connect(component("_SimpleMovingAverage_LONG"), port("OUT"), component("_TradeMultiplexer"), port("IN",9));
	    
	    
	   
	    
	    
	    connect(component("_TradeMultiplexer"), port("OUT"), component("_PortolioManager"), port("IN",0));
	  
	    
	    connect(component("_PortolioManager"), port("OUT"), component("_ActionManager"), port("IN"));
	    
	    
	    initialize(Double.valueOf(12), component("_SimpleMovingAverage_SHORT"), port("WINDOW"));
	    initialize(Double.valueOf(26), component("_SimpleMovingAverage_LONG"), port("WINDOW"));
	    //connect(component("_DataFeeder"), port("OUT"), component("_Write_text_to_pane"), port("IN"));
	  }
	
	public static void main(final String[] argv) throws Exception {
	    OriginalTradeNetwork aa = new OriginalTradeNetwork();
	 
	    aa.go();
	  }

}
