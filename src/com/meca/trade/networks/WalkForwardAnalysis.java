package com.meca.trade.networks;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.to.Constants;
import com.meca.trade.to.IMarketData;
import com.meca.trade.to.MarketDataGenerator;

public class WalkForwardAnalysis {
	MarketDataGenerator generator = null;
	HashMap<String, Parameter> wfaParamMap;
	Document doc = null;
	int testWindowSize =  0;
	

	public void createOptimizationEnvironment() throws Exception{
		SAXBuilder builder = new SAXBuilder();

		try {
			doc = builder.build(Constants.INPUT_DIRECTORY + File.separator + "NetworkOptimization.xml");

			wfaParamMap = new HashMap<String, Parameter>();
			Element geneticConfiguration = doc.getRootElement()
					.getChildren("wfa-configuration").get(0);
			Iterator itr = geneticConfiguration.getChildren().iterator();

			while (itr.hasNext()) {
				Element elem = (Element) itr.next();
				String paramName = elem.getAttribute("name").getValue();
				wfaParamMap.put(paramName,
						new Parameter(paramName, elem.getAttribute("type")
								.getValue(), elem.getAttribute("value")
								.getValue()));
			}
		} catch (Exception e) {
			throw e;
		} finally{
			
		}

	}
	
	public WalkForwardAnalysis(MarketDataGenerator generator) throws Exception {
		this.generator = generator;
		
		createOptimizationEnvironment();
		
		testWindowSize = this.generator.getMarketDataSize();
		
		int counter = 0;
		
		int optimizationWindowSize = ((Integer)wfaParamMap.get("OPTIMIZATION_WINDOW_PERCENTAGE").getValue())*testWindowSize/100;
		int wfaWindowSize = ((Integer)wfaParamMap.get("WFA_WINDOW_PERCENTAGE").getValue())*testWindowSize/100;
		
		int optimizationCycleFromIndex = 0;
		int optimizationCycleToIndex = 0;
		int wfaCycleFromIndex = 0;
		int wfaCycleToIndex = 0;
		
		for(counter=0; ;counter++){
			System.out.println("counter" + counter);

			optimizationCycleFromIndex = counter*wfaWindowSize;
			optimizationCycleToIndex = optimizationCycleFromIndex + optimizationWindowSize;
			wfaCycleFromIndex = optimizationCycleToIndex;
			wfaCycleToIndex = wfaCycleFromIndex + wfaWindowSize;
			
			if (wfaCycleFromIndex >= testWindowSize) break;
			
			System.out.println("WalkForwardAnalysis " + "optimizationCycleFromIndex:" + optimizationCycleFromIndex + " optimizationCycleToIndex:" + optimizationCycleToIndex + " wfaCycleFromIndex:" + wfaCycleFromIndex + " wfaCycleToIndex" + wfaCycleToIndex);
			
			IMarketData marketData = generator.getMarketData(optimizationCycleFromIndex, optimizationCycleToIndex);
			
			System.out.println("WalkForwardAnalysis.Optimization Started " + marketData.getPeriodStart() + " - " + marketData.getPeriodEnd());
			NewOptimizer optimizer = new NewOptimizer(marketData);
			optimizer.iterate();
			
			System.out.println("WalkForwardAnalysis.Optimization Ended " );
			
			IMarketData walkForwardMarketData = generator.getMarketData(wfaCycleFromIndex, wfaCycleToIndex);
			
			System.out.println("WalkForwardAnalysis.WalkForwardWindow Started " + walkForwardMarketData.getPeriodStart() + " - " + walkForwardMarketData.getPeriodEnd());
			
			optimizer.getMankind().setMarketData(walkForwardMarketData);
			optimizer.reRunBestIndividuals();
			System.out.println("WalkForwardAnalysis.WalkForwardWindow Ended ");
			
			
			
			
		}
	}
		
		
	

	
	
}
