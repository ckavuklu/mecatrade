package com.meca.trade.networks;

import java.util.Random;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.meca.trade.to.TradeUtils;


public class IndicatorParameter implements java.lang.Cloneable{
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	
	protected IndicatorParameter(IndicatorParameter that) {
		this.networkName = that.networkName;
		this.name =that.name;
		this.port = that.port;
		this.type = that.type;
		this.start = that.start;
		this.end = that.end;
		this.value = that.getValue();
	}

	String networkName;
	String name;
	String port;
	String type;
	String start;
	String end;
	Object value;

	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(name);
		builder.append(":");
		builder.append(getValue());
		
		return builder.toString();
	}
	
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
	
	public String getStringValue(){
		String result = null;
		 if(!type.equalsIgnoreCase("String")){
			 if (value instanceof Integer){
				 result = String.valueOf(value);
			 }else if(value instanceof Double){
				 result = String.valueOf(Math.round((Double)value));
			 }else{
				 result = "null";
			 }
		 }
	      else
	    	  result = (String)value;
		 
		 return result;
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
