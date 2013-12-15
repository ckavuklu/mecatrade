package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Optimizer {
	
	HashMap<String, Parameter> paramMap;
	NetworkTemplateGenerator networkTemplates = null;
	IndicatorManager indicatorMng = new IndicatorManager();
	
	public Optimizer(String fileName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	
		paramMap = new HashMap<String,Parameter>();
		
		SAXBuilder builder = new SAXBuilder();
		
        try {
			Document doc = builder.build(fileName);
			
		
			Element configuration = doc.getRootElement().getChildren("configuration").get(0);
			populateParameterList(configuration);
			
			networkTemplates = new NetworkTemplateGenerator("NetworkDefinition.xml",paramMap);

			Element indicators = doc.getRootElement().getChildren("indicators").get(0);
			populateIndicators(indicators);
			
			
			populateNetworkDefinitions();
			
			networkTemplates.runNetworks();
			
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
	
	private void populateNetworkDefinitions(){
		List<TradeNetwork> networkList = networkTemplates.getNetworkList();
		HashMap<String,List<IndicatorParameter>> indicatorMap = indicatorMng.getIndicatorMap();
		
		for(TradeNetwork network:networkList){
			List<IndicatorParameter> indicatorList = indicatorMap.get(network.getNetworkName());
			for(IndicatorParameter indicator:indicatorList){
				network.addInitialization(indicator.randomize(), indicator.getName(), indicator.getPort());
			}
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
