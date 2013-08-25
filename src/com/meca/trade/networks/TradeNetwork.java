package com.meca.trade.networks;

import com.jpmorrsn.fbp.engine.Network;

public class TradeNetwork extends Network {

	@Override
	  protected void define() {
	    //component("_Discard", com.jpmorrsn.fbp.components.Discard.class);
	    //component("_Write_text_to_pane", com.jpmorrsn.fbp.components.ShowText.class);
	    
		// Trade Data Components
		component("_DataSource", com.meca.trade.components.DataSource.class);
	    component("_DataFeeder", com.meca.trade.components.DataFeeder.class);
	    
	    component("_QuotePrice_O", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_C", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_H", com.meca.trade.components.QuotePrice.class);
	    component("_QuotePrice_L", com.meca.trade.components.QuotePrice.class);

	    // Trade Data Initializations
	    initialize("O", component("_QuotePrice_O"), port("PRICETYPE"));
	    initialize("C", component("_QuotePrice_C"), port("PRICETYPE"));
	    initialize("H", component("_QuotePrice_H"), port("PRICETYPE"));
	    initialize("L", component("_QuotePrice_L"), port("PRICETYPE"));
	    initialize("Minute", component("_DataFeeder"), port("SCHEDULETYPE"));
	    initialize(Integer.valueOf(2), component("_DataFeeder"), port("SCHEDULEPERIOD"));
	    initialize("EURUSD.txt", component("_DataSource"), port("FILENAME"));

	    
	    // Indicator Components
	    component("_SimpleMovingAverage", com.meca.trade.components.SimpleMovingAverage.class);
	    
	    initialize(Integer.valueOf(10), component("_SimpleMovingAverage"), port("WINDOW"));

	    
	    // Stragety
	    component("_TradeMultiplexer", com.meca.trade.components.TradeMultiplexer.class);
	    
	    connect(component("_DataSource"), port("OUT"), component("_DataFeeder"), port("TRADEDATA"));
	    
	    connect(component("_DataFeeder"), port("OUT",0), component("_QuotePrice_O"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",1), component("_QuotePrice_C"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",2), component("_QuotePrice_H"), port("TRADEDATA"));
	    connect(component("_DataFeeder"), port("OUT",3), component("_QuotePrice_L"), port("TRADEDATA"));
	    
	    connect(component("_QuotePrice_O"), port("OUT",0), component("_TradeMultiplexer"), port("IN",0));
	    connect(component("_QuotePrice_C"), port("OUT",0), component("_TradeMultiplexer"), port("IN",1));
	    connect(component("_QuotePrice_H"), port("OUT",0), component("_TradeMultiplexer"), port("IN",2));
	    connect(component("_QuotePrice_L"), port("OUT",0), component("_TradeMultiplexer"), port("IN",3));
	  
	    connect(component("_SimpleMovingAverage"), port("OUT"), component("_TradeMultiplexer"), port("IN",4));
	    connect(component("_QuotePrice_C"), port("OUT",1), component("_SimpleMovingAverage"), port("DATA"));
	    
	    
	    //connect(component("_DataFeeder"), port("OUT"), component("_Write_text_to_pane"), port("IN"));
	  }
	
	public static void main(final String[] argv) throws Exception {
	    new TradeNetwork().go();
	  }

}