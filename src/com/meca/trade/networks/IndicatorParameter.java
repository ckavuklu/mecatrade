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
	Object value;

	
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
	
	public Object getValue(){
       
        if(type.equalsIgnoreCase("Double")){
        	
        	if (value instanceof Integer)
        		return (Double)((Integer)value*1d);

        	else if (value instanceof Double)
        	return (Double)(value);
        	
        	else return "Not Yet";
        			}
      else
			return "Not Yet";

	}
	
	public void setValue(Object newValue){
        value = newValue;
	}


	public Object randomize(){
        Random rand = new Random();
     
        if(type.equalsIgnoreCase("Double")){
				value = rand.nextInt(Integer.valueOf(end)-Integer.valueOf(start)+1)+Integer.valueOf(start);
		}else
				value = "Not Yet";
		
		return (Double)((Integer)value*1d);
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
