package com.meca.trade.networks;

import java.util.Date;

import com.meca.trade.to.Constants;
import com.meca.trade.to.TradeUtils;

public class Parameter {
	public String getName() {
		return name;
	}

	String name;
	String type;
	String value;
	Object result;
	
	public String getType() {
		return type;
	}

	public Parameter(String name, String type, String value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public Object getValue() {
		if(type.equalsIgnoreCase("String")){
			result = String.valueOf(value);
		}else if(type.equalsIgnoreCase("Double")){
			result = Double.valueOf(value);
		}else if(type.equalsIgnoreCase("Integer")){
			result = Integer.valueOf(value);
		}else if(type.equalsIgnoreCase("Date")){
			result = TradeUtils.getTime(value);
		}else if(type.equalsIgnoreCase(Constants.STOP_LOSS_VALUE_TYPE_EQUITY_PERCENTAGE)){
			result = Double.valueOf(value);
		}else if(type.equalsIgnoreCase(Constants.STOP_LOSS_VALUE_TYPE_POSITION_PERCENTAGE)){
			result = Double.valueOf(value);
		}else if(type.equalsIgnoreCase(Constants.STOP_LOSS_VALUE_TYPE_POINT_TYPE)){
			result = Double.valueOf(value);
		}else
			result = "Not Yet";

		return result;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

	
}
