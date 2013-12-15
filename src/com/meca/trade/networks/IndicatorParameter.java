package com.meca.trade.networks;

import java.util.Random;

import com.meca.trade.to.TradeUtils;


public class IndicatorParameter {
	String networkName;
	String name;
	String port;
	String type;
	String start;
	String end;

	
	public IndicatorParameter(String networkName, String name, String port,
			String type, String start, String end) {
		super();
		this.networkName = networkName;
		this.name = name;
		this.port = port;
		this.type = type;
		this.start = start;
		this.end = end;
	}


	public Object randomize(){
		Object result;
		
        Random rand = new Random();
     
        if(type.equalsIgnoreCase("Double")){
				result = rand.nextInt(Integer.valueOf(end)-Integer.valueOf(start)+1)+Integer.valueOf(start);
		}else
				result = "Not Yet";
		
		return (Double)((Integer)result*1d);
	}
	
	public String getNetworkName() {
		return networkName;
	}



	public String getName() {
		return name;
	}


	public String getPort() {
		return port;
	}

	

}
