package com.meca.trade.networks;

import com.jpmorrsn.fbp.engine.Network;

public class TradeNetwork extends Network {
	private String networkName;

	

	public String getNetworkName() {
		return networkName;
	}



	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}



	@Override
	  protected void define() {
	    
	  }

}
