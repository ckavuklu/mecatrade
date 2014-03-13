package com.meca.trade.networks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.Network;
import com.meca.trade.to.Account;
import com.meca.trade.to.AccountStatusType;
import com.meca.trade.to.Constants;
import com.meca.trade.to.CurrencyType;
import com.meca.trade.to.IMarketData;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.IStrategy;
import com.meca.trade.to.ITestTradeDataSet;
import com.meca.trade.to.ITrader;
import com.meca.trade.to.MarketType;
import com.meca.trade.to.PerformanceReportManager;
import com.meca.trade.to.PositionManager;
import com.meca.trade.to.TestTradeDataSet;

public class TradeNetwork extends Network implements Comparable<TradeNetwork> {
	
	private String networkName;
	private HashMap<String, Parameter> networkConfigurationParameterMap;
	private PerformanceReportManager reportManager = null;
	private Account account = null;
	private IPositionManager posManager = null;
	private TestTradeDataSet dataSet  = null;
	private ITrader trader = null;
	private IStrategy strategy = null;
	private List<IndicatorParameter> indicatorParameterList = null;
	private Double fitnessValue;
	
	String uuid = null;
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(networkName);
		builder.append(Constants.SEPARATOR);
		builder.append(uuid);
		builder.append(Constants.SEPARATOR);
		
		for(IndicatorParameter parameter:indicatorParameterList){
			builder.append(parameter);
			builder.append(Constants.SPACE);
		}
		
		builder.append("fitnessValue");
		builder.append(Constants.SEPARATOR);
		builder.append(getFitnessValue());
		
		
		return builder.toString();
	}
	
	@Override
    public boolean equals(Object obj)
    {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof TradeNetwork))
            return false;

        TradeNetwork rhs = (TradeNetwork) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(uuid, rhs.uuid).
            isEquals();
    }
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public int compareTo(TradeNetwork arg0) {
		
		if (getFitnessValue() < arg0.getFitnessValue()) return -1;
        if (getFitnessValue() > arg0.getFitnessValue()) return 1;
        return 0;
	}
	
	public TradeNetwork clone(){
		TradeNetwork result = new TradeNetwork();
		
		Map<String,Component> map = this.getComponents();
		Map<String,Component> newMap = new HashMap<String,Component>(map);
		
		result.setComponents(newMap);
		
		return result;
	}
	
	public void randIndicatorParametersAndInitialize() {
    
        for(IndicatorParameter indicator:indicatorParameterList){
			addInitialization(indicator.randomize(), indicator.getName(), indicator.getPort());
		}
    }
	
	
	
	public void initializeByIndicatorParameterValues() {
	    
        for(IndicatorParameter indicator:indicatorParameterList){
			addInitialization(indicator.getValue(), indicator.getName(), indicator.getPort());
		}
    }

	
	public void mutate() {
        Random rand = new Random();
        
        int index = rand.nextInt(indicatorParameterList.size());

        this.indicatorParameterList.get(index).randomize();    
    }
	
	
	
	public Double evaluate(Boolean createGraphData) throws Exception {
		  
		posManager.setGraphLog(createGraphData);
		
		this.go();
		
        this.setFitnessValue(reportManager.getFitnessValue());

        return this.getFitnessValue();
    }

	
	public Double getIndicatorParameterValue(Integer index) {
		return (Double)indicatorParameterList.get(index).getValue();
	}


	public void setIndicatorParameterValue(Integer index, Double value) {
		indicatorParameterList.get(index).setValue(value);
	}
	
	public Double getFitnessValue() {
		return fitnessValue;
	}



	public void setFitnessValue(Double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}



	public List<IndicatorParameter> getIndicatorParameterList() {
		return indicatorParameterList;
	}



	public void setIndicatorParameterList(
			List<IndicatorParameter> indicatorParameterList) {
		this.indicatorParameterList = indicatorParameterList;
	}



	public String getNetworkName() {
		return networkName;
	}



	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}


	@Override
	  protected void define() {
	    
	  }
	
	  public void addComponent(String name, Class type) {
		  super.component(name, type);
	  }
	  
	  public void addConnection(String fromComponent, String fromPort, Integer fromPortNo, String toComponent, String toPort, Integer toPortNo) {
		  if(fromPortNo==null && toPortNo==null){
			  super.connect(component(fromComponent), port(fromPort), component(toComponent), port(toPort));
		  }else if(fromPortNo!=null && toPortNo==null){
			  super.connect(component(fromComponent), port(fromPort,fromPortNo), component(toComponent), port(toPort));
			 
		  }else if(fromPortNo==null && toPortNo!=null){
			  super.connect(component(fromComponent), port(fromPort), component(toComponent), port(toPort,toPortNo));
			 
		  }else if(fromPortNo!=null && toPortNo!=null){
			  super.connect(component(fromComponent), port(fromPort,fromPortNo), component(toComponent), port(toPort,toPortNo));
			 
		  }
	  }
	  
	  public void addInitialization(Object obj, String compName, String portName) {
		  super.initialize(obj, component(compName), port(portName));
	  }
	  
	  public void init(HashMap<String, Parameter> parameterMap, IMarketData marketDataInterface){
	        this.networkConfigurationParameterMap = parameterMap;
	       
	        Double accountBalance = (Double)networkConfigurationParameterMap.get("ACCOUNT_BALANCE").getValue();

	        uuid = UUID.randomUUID().toString();
	        
	        indicatorParameterList = new ArrayList<IndicatorParameter>();
	        
			reportManager = new PerformanceReportManager(marketDataInterface,accountBalance);
			
			account = new Account(CurrencyType.USD,"5678",accountBalance,AccountStatusType.OPEN);
			
			/*
			IndicatorSet set = new IndicatorSet();
			set.addIndicator("SMASHORT", 4);
			set.addIndicator("SMALONG", 9);
			strategy = new SMAStrategy(set);
			
			IndicatorSet stochasticKLine = new IndicatorSet();
			stochasticKLine.addIndicator("KLINE", 10);
			IndicatorSet stochasticDLine = new IndicatorSet();
			stochasticDLine.addIndicator("DLINE", 11);
			StochasticStrategy stochasticStrategy = new StochasticStrategy(stochasticKLine,stochasticDLine,80d,20d);
			*/
				
			posManager = new PositionManager(null,account,reportManager,MarketType.EURUSD,networkName);
			//dataSet = new TestTradeDataSet((String)parameterMap.get("INPUT_TEST_TRADE_DATA_FILE_NAME").getValue());
			//TurtleStrategy turtleStrategy = new TurtleStrategy(shortSet);
			
			//trader = new BaseTrader(posManager);
	  }



	  public void init(HashMap<String, Parameter> parameterMap){
	        this.networkConfigurationParameterMap = parameterMap;
	       
	        Double accountBalance = (Double)parameterMap.get("ACCOUNT_BALANCE").getValue();

	        uuid = UUID.randomUUID().toString();
	        
	        indicatorParameterList = new ArrayList<IndicatorParameter>();
	        
			reportManager = new PerformanceReportManager(parameterMap);
			
			account = new Account(CurrencyType.USD,"5678",accountBalance,AccountStatusType.OPEN);
			
			/*
			IndicatorSet set = new IndicatorSet();
			set.addIndicator("SMASHORT", 4);
			set.addIndicator("SMALONG", 9);
			strategy = new SMAStrategy(set);
			
			IndicatorSet stochasticKLine = new IndicatorSet();
			stochasticKLine.addIndicator("KLINE", 10);
			IndicatorSet stochasticDLine = new IndicatorSet();
			stochasticDLine.addIndicator("DLINE", 11);
			StochasticStrategy stochasticStrategy = new StochasticStrategy(stochasticKLine,stochasticDLine,80d,20d);
			*/
				
			posManager = new PositionManager(null,account,reportManager,MarketType.EURUSD,networkName);
			//dataSet = new TestTradeDataSet((String)parameterMap.get("INPUT_TEST_TRADE_DATA_FILE_NAME").getValue());
			//TurtleStrategy turtleStrategy = new TurtleStrategy(shortSet);
			
			//trader = new BaseTrader(posManager);
	  }


	public PerformanceReportManager getReportManager() {
		return reportManager;
	}

	public ITrader getTrader() {
		return trader;
	}



	public void setTrader(ITrader trader) {
		this.trader = trader;
	}



	public ITestTradeDataSet getDataSet() {
		return dataSet;
	}



	public IPositionManager getPosManager() {
		return posManager;
	}



	public IStrategy getStrategy() {
		return strategy;
	}



	public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}
	  
	

}
