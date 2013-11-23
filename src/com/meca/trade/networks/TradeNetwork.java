package com.meca.trade.networks;

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
import com.meca.trade.to.SMAStrategy;
import com.meca.trade.to.TestTradeDataSet;

public class TradeNetwork extends Network {

	@Override
	  protected void define() {
	    //component("_Discard", com.jpmorrsn.fbp.components.Discard.class);
	    //component("_Write_text_to_pane", com.jpmorrsn.fbp.components.ShowText.class);
		
		final String INPUT_MARKET_DATA_FILE_NAME = "TESTDATA.txt";
		final String INPUT_TEST_TRADE_DATA_FILE_NAME = "TestTradeSet-1";
		
		PerformanceReportManager reportManager = new PerformanceReportManager(INPUT_MARKET_DATA_FILE_NAME,INPUT_TEST_TRADE_DATA_FILE_NAME);
		
		Account usdAcc = new Account(CurrencyType.USD,"5678",100000d,AccountStatusType.OPEN);
		
		
		IndicatorSet shortSet = new IndicatorSet();
		shortSet.addIndicator("SMA", 4);
		
		IndicatorSet longSet = new IndicatorSet();
		longSet.addIndicator("SMA", 9);
		

		
			
		IPositionManager posManager = new PositionManager(null,usdAcc,reportManager,MarketType.EURUSD);
		TestTradeDataSet dataSet = new TestTradeDataSet(INPUT_TEST_TRADE_DATA_FILE_NAME);
		//TurtleStrategy turtleStrategy = new TurtleStrategy(shortSet);
		SMAStrategy smaStrategy = new SMAStrategy(shortSet,longSet);
		
		BaseTrader base = new BaseTrader(posManager);
		
	    
		// Trade Data Components
		component("_DataSource", com.meca.trade.components.TestDataSource.class);
	    component("_DataFeeder", com.meca.trade.components.DataFeeder.class);
	    
	    component("_QuotePrice_O", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_C", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_H", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_L", com.meca.trade.components.QuotePrice.class);
	    
	    component("_Kicker", com.meca.trade.components.Kicker.class);
	    
	    // Trade Data Initializations
	    initialize("O", component("_QuotePrice_O"), port("PRICETYPE"));
	    initialize("C", component("_QuotePrice_C"), port("PRICETYPE"));
	    initialize("H", component("_QuotePrice_H"), port("PRICETYPE"));
	    initialize("L", component("_QuotePrice_L"), port("PRICETYPE"));
	   
	    initialize("ALL", component("_DataFeeder"), port("SCHEDULETYPE"));
	    //initialize("Minute", component("_DataFeeder"), port("SCHEDULETYPE"));
	    initialize(Integer.valueOf(2), component("_DataFeeder"), port("SCHEDULEPERIOD"));
	    initialize(INPUT_MARKET_DATA_FILE_NAME, component("_DataSource"), port("FILENAME"));

	    
	    // Indicator Components
	    component("_SimpleMovingAverage_SHORT", com.meca.trade.components.SimpleMovingAverage.class);
	    component("_SimpleMovingAverage_LONG", com.meca.trade.components.SimpleMovingAverage.class);
	   /* component("_ExponentialMovingAverage", com.meca.trade.components.ExponentialMovingAverage.class);
	    component("_MACD", com.meca.trade.networks.subnets.MACDSubNetwork.class);*/
	    
	    
	    initialize(Double.valueOf(3), component("_SimpleMovingAverage_SHORT"), port("WINDOW"));
	    initialize(Double.valueOf(6), component("_SimpleMovingAverage_LONG"), port("WINDOW"));
/*	    initialize(Double.valueOf(5), component("_ExponentialMovingAverage"), port("WINDOW"));
	    initialize(Double.valueOf(12), component("_MACD"), port("SHORTEMAPERIOD"));
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
	    initialize(smaStrategy, component("_TradeMultiplexer"), port("STRATEGY"));
	    
	    connect(component("_DataSource"), port("OUT"), component("_DataFeeder"), port("TRADEDATA"));
	    
	    connect(component("_DataFeeder"), port("OUT",0), component("_QuotePrice_O"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",1), component("_QuotePrice_C"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",2), component("_QuotePrice_H"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",3), component("_QuotePrice_L"), port("TRADEDATA"));

		connect(component("_Kicker"), port("OUT",0), component("_QuotePrice_O"), port("KICKOFF"));
	    connect(component("_Kicker"), port("OUT",1), component("_QuotePrice_C"), port("KICKOFF"));
	    connect(component("_Kicker"), port("OUT",2), component("_QuotePrice_H"), port("KICKOFF"));
	    connect(component("_Kicker"), port("OUT",3), component("_QuotePrice_L"), port("KICKOFF"));
	    	    
	    
	    connect(component("_ActionManager"), port("CLOCKTICK",0), component("_QuotePrice_O"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",1), component("_QuotePrice_C"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",2), component("_QuotePrice_H"), port("CLOCKTICK"));
	    connect(component("_ActionManager"), port("CLOCKTICK",3), component("_QuotePrice_L"), port("CLOCKTICK"));
	    
	    connect(component("_QuotePrice_C"), port("OUT",2), component("_SimpleMovingAverage_SHORT"), port("DATA"));
	    connect(component("_QuotePrice_C"), port("OUT",3), component("_SimpleMovingAverage_LONG"), port("DATA"));
	    
	    
	    connect(component("_QuotePrice_O"), port("OUT",0), component("_TradeMultiplexer"), port("IN",0));
	    connect(component("_QuotePrice_C"), port("OUT",0), component("_TradeMultiplexer"), port("IN",1));
	    connect(component("_QuotePrice_H"), port("OUT",0), component("_TradeMultiplexer"), port("IN",2));
	    connect(component("_QuotePrice_L"), port("OUT",0), component("_TradeMultiplexer"), port("IN",3));
	  
	    connect(component("_SimpleMovingAverage_SHORT"), port("OUT"), component("_TradeMultiplexer"), port("IN",4));
/*	    connect(component("_ExponentialMovingAverage"), port("OUT",0), component("_TradeMultiplexer"), port("IN",5));
	    connect(component("_MACD"), port("MACDLINE"), component("_TradeMultiplexer"), port("IN",6));
	    connect(component("_MACD"), port("SIGNALLINE"), component("_TradeMultiplexer"), port("IN",7));
	    connect(component("_MACD"), port("HISTOGRAM"), component("_TradeMultiplexer"), port("IN",8));*/
	    connect(component("_SimpleMovingAverage_LONG"), port("OUT"), component("_TradeMultiplexer"), port("IN",9));
	    
	    
	    
	    connect(component("_TradeMultiplexer"), port("OUT"), component("_PortolioManager"), port("IN",0));
	  
	    
	    connect(component("_PortolioManager"), port("OUT"), component("_ActionManager"), port("IN"));
	    
	    //connect(component("_DataFeeder"), port("OUT"), component("_Write_text_to_pane"), port("IN"));
	  }
	
	public static void main(final String[] argv) throws Exception {
	    new TradeNetwork().go();
	  }

}
