package com.meca.trade.networks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.to.Constants;
import com.meca.trade.to.FileReportGenerator;
import com.meca.trade.to.IMarketData;
import com.meca.trade.to.IReportLogger;
import com.meca.trade.to.MarketDataGenerator;
import com.meca.trade.to.PerformanceKPIS;
import com.meca.trade.to.TradeUtils;

public class WalkForwardAnalysis {
	MarketDataGenerator generator = null;
	HashMap<String, Parameter> wfaParamMap;
	Document doc = null;
	private HashMap<String,IReportLogger>  periodBasedPerformanceData = null;
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
		

		HashMap<String,List<PerformanceKPIS>> listOfKPIs = new HashMap<String,List<PerformanceKPIS>>();
		List<TradeNetwork> bestNetworks = null;
		
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
			
			bestNetworks = optimizer.reRunBestIndividuals();
			
			HashMap<String,Double> networkWFE = new HashMap<String,Double>();
		
			for(TradeNetwork network:bestNetworks ){
				
				List<PerformanceKPIS> tempListKPIs = listOfKPIs.get(network.getNetworkName());
				if (tempListKPIs == null) tempListKPIs = new ArrayList<PerformanceKPIS>();
	
				tempListKPIs.add(network.getReportManager().getPerformanceKPIs());
				networkWFE.put(network.getNetworkName(),network.getReportManager().getPerformanceKPIs().getAnnualizedNetProfit());
				
				listOfKPIs.put(network.getNetworkName(),tempListKPIs);
				
			
			}
			
			
			System.out.println("WalkForwardAnalysis.Optimization Ended " );
			
			IMarketData walkForwardMarketData = generator.getMarketData(wfaCycleFromIndex, wfaCycleToIndex);
			
			System.out.println("WalkForwardAnalysis.WalkForwardWindow Started " + walkForwardMarketData.getPeriodStart() + " - " + walkForwardMarketData.getPeriodEnd());
			
			optimizer.getMankind().setMarketData(walkForwardMarketData);
		
			bestNetworks = optimizer.reRunBestIndividuals();
			
			for(TradeNetwork network:bestNetworks ){
				
				List<PerformanceKPIS> tempListKPIs = listOfKPIs.get(network.getNetworkName());
				if (tempListKPIs == null) tempListKPIs = new ArrayList<PerformanceKPIS>();
				
				Double wfeWindowAnnualizedProfit = network.getReportManager().getPerformanceKPIs().getAnnualizedNetProfit();
				Double optimizationWindowAnnualizedProfit = networkWFE.get(network.getNetworkName());
				
				Double percentage =  ((wfeWindowAnnualizedProfit - optimizationWindowAnnualizedProfit) / (Math.abs(optimizationWindowAnnualizedProfit)==0?1:Math.abs(optimizationWindowAnnualizedProfit)))*100d;
				
				network.getReportManager().getPerformanceKPIs().setWfe(TradeUtils.roundDownDigits(percentage,2));
				
				tempListKPIs.add(network.getReportManager().getPerformanceKPIs());
				listOfKPIs.put(network.getNetworkName(),tempListKPIs);

			}
			
			System.out.println("WalkForwardAnalysis.WalkForwardWindow Ended ");
			
			
		}
		
		
		this.periodBasedPerformanceData = new HashMap<String,IReportLogger>();
		
		int count = 0;
		
		for(TradeNetwork network:bestNetworks){

			FileReportGenerator tempFileGenerator = new FileReportGenerator();
			tempFileGenerator.initializeLogger(network.getNetworkName() + "_WFA_Periodic_Performance.xls");
			periodBasedPerformanceData.put(network.getNetworkName(),tempFileGenerator);
			
		}
		
		
		Set<Entry<String, List<PerformanceKPIS>>> set = listOfKPIs.entrySet();

		for(Entry<String, List<PerformanceKPIS>> e: set){
		
			if(e.getValue().size() > 0){
				periodBasedPerformanceData.get(e.getKey()).writeLog(e.getValue().get(0).getHeaders());
			}
			
			for(PerformanceKPIS kpi:e.getValue()){
				periodBasedPerformanceData.get(e.getKey()).writeLog(kpi.getData());
			}	
			
			periodBasedPerformanceData.get(e.getKey()).finalizeLogger();
		}
		
		
		
		
	}
		
		
	

	
	
}
