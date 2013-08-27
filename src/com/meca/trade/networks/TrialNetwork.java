package com.meca.trade.networks;

import com.jpmorrsn.fbp.engine.Network;

public class TrialNetwork extends Network {

	@Override
	protected void define() {
		// component("_Discard", com.jpmorrsn.fbp.components.Discard.class);
		// component("_Write_text_to_pane",
		// com.jpmorrsn.fbp.components.ShowText.class);

		// Trade Data Components
		component("_DataSource", com.meca.trade.components.TestDataSource.class);
		component("_DataFeeder", com.meca.trade.components.DataFeeder.class);

		component("_QuotePrice_C", com.meca.trade.components.QuotePrice.class);

		// Trade Data Initializations
		initialize("C", component("_QuotePrice_C"), port("PRICETYPE"));

		initialize("ALL", component("_DataFeeder"), port("SCHEDULETYPE"));
		initialize(Integer.valueOf(2), component("_DataFeeder"),
				port("SCHEDULEPERIOD"));
		initialize("TESTDATA_MACD.txt", component("_DataSource"),
				port("FILENAME"));

		// Indicator Components
		component("_MACD",
				com.meca.trade.networks.subnets.TrialSubNetwork.class);

		// Stragety
		component("_TradeMultiplexer",
				com.meca.trade.components.TradeMultiplexer.class);

		connect(component("_DataSource"), port("OUT"),
				component("_DataFeeder"), port("TRADEDATA"));

		connect(component("_DataFeeder"), port("OUT", 0),
				component("_QuotePrice_C"), port("TRADEDATA"));

		connect(component("_QuotePrice_C"), port("OUT", 0), component("_MACD"),
				port("INPUT"));

	
		connect(component("_MACD"), port("MACDLINE"),
				component("_TradeMultiplexer"), port("IN", 0));

		// connect(component("_DataFeeder"), port("OUT"),
		// component("_Write_text_to_pane"), port("IN"));
	}

	public static void main(final String[] argv) throws Exception {
		new TrialNetwork().go();
	}

}
