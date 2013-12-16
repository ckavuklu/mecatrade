package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Optimizer {
	
	HashMap<String, Parameter> paramMap;
	HashMap<String, Parameter> geneticParamMap;
	HashMap<String,List<TradeNetwork>> m_population;
	HashMap<String,Double> totalFitness;
	
	Integer pop_size, max_iteration;
	NetworkTemplateGenerator networkTemplates = null;
	IndicatorManager indicatorMng = new IndicatorManager();
	
	public Optimizer(String fileName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	
		paramMap = new HashMap<String,Parameter>();
		geneticParamMap = new HashMap<String,Parameter>();
		
		SAXBuilder builder = new SAXBuilder();
		
        try {
			Document doc = builder.build(fileName);
			
		
			Element configuration = doc.getRootElement().getChildren("configuration").get(0);
			populateParameterList(configuration);
			
			Element geneticConfiguration = doc.getRootElement().getChildren("genetic-configuration").get(0);
			populateGeneticParameterList(geneticConfiguration);
			
			pop_size = (Integer)geneticParamMap.get("POP_SIZE").getValue() + (Integer)geneticParamMap.get("ELITISM_K").getValue();
			max_iteration = (Integer)geneticParamMap.get("MAX_ITER").getValue();
			
			networkTemplates = new NetworkTemplateGenerator("NetworkDefinition.xml",paramMap,pop_size);

			Element indicators = doc.getRootElement().getChildren("indicators").get(0);
			populateIndicators(indicators);
			
			setNetworkIndicatorParameterList();
			
			
			m_population = new HashMap<String,List<TradeNetwork>>();
			totalFitness = new HashMap<String,Double>();
			
			
			int fromIndex = 0;
			while(fromIndex < networkTemplates.getNumOfNetworkTemplates()){
				m_population.put(networkTemplates.networkList.get(fromIndex*pop_size).getNetworkName(), networkTemplates.networkList.subList(fromIndex*pop_size, (fromIndex+1)*pop_size));
				totalFitness.put(networkTemplates.networkList.get(fromIndex*pop_size).getNetworkName(), 0d);
				
				for(TradeNetwork network:m_population.get(networkTemplates.networkList.get(fromIndex*pop_size).getNetworkName())){
					network.randIndicatorParametersAndInitialize();
					Double networkFitness = network.evaluate();
					
					totalFitness.put(networkTemplates.networkList.get(fromIndex*pop_size).getNetworkName(), totalFitness.get(networkTemplates.networkList.get(fromIndex*pop_size).getNetworkName())+ networkFitness);
				}
				
				fromIndex++;
			}
			
			
			
			
			
			
			//networkTemplates.runNetworks();
			
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	
	private void populateGeneticParameterList(Element configuration) {
		Iterator itr = configuration.getChildren().iterator();
		
		 while (itr.hasNext()) {
	            Element elem = (Element) itr.next();
	            String paramName = elem.getAttribute("name").getValue();
	            geneticParamMap.put(paramName,new Parameter(paramName, elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
	         }
		 
	}

	private void setNetworkIndicatorParameterList(){
		List<TradeNetwork> networkList = networkTemplates.getNetworkList();
		HashMap<String,List<IndicatorParameter>> indicatorMap = indicatorMng.getIndicatorMap();
		
		for(TradeNetwork network:networkList){
			List<IndicatorParameter> indicatorList = indicatorMap.get(network.getNetworkName());
			network.setIndicatorParameterList(indicatorList);
			/*
			for(IndicatorParameter indicator:indicatorList){
				network.addInitialization(indicator.randomize(), indicator.getName(), indicator.getPort());
			}*/
		}
		
	}
	
	private void populateParameterList(Element configuration){
		Iterator itr = configuration.getChildren().iterator();
		
		 while (itr.hasNext()) {
	            Element elem = (Element) itr.next();
	            String paramName = elem.getAttribute("name").getValue();
	            paramMap.put(paramName,new Parameter(paramName, elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
	         }
	}
	
	
	private void populateIndicators(Element indicators) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException {
		
		Iterator itr = indicators.getChildren().iterator();

		while (itr.hasNext()) {
			
			
			Element indicator =  (Element) itr.next();
			String networkName = indicator.getAttribute("network").getValue();
			String componentName = indicator.getAttribute("name").getValue();
			String portName = indicator.getAttribute("port").getValue();
			
			Element interval = indicator.getChild("interval");
			String start = interval.getAttribute("start").getValue();
			String end = interval.getAttribute("end").getValue();
			String paramType = interval.getAttribute("type").getValue();
		
			IndicatorParameter indicatorParam = new IndicatorParameter(networkName,componentName, portName, paramType, start, end);
			
			indicatorMng.addIndicator(indicatorParam);
			
		}
	}
	
	
	
	//public static HashMap<String name,>

	public static void main(String[] args) {
		
		try {
			
			Optimizer optimizer = new Optimizer("NetworkGeneration.xml");
			
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
