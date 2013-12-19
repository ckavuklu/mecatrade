package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.IStrategy;
import com.meca.trade.to.ITrader;

public class NetworkTemplateGenerator {
	HashMap<String, Parameter> paramMap;
	List<TradeNetwork> networkList;
	Integer numOfNetworkTemplates = 0;
	private Integer populationSize;
	Document doc = null;
	
	public Integer getNumOfNetworkTemplates() {
		return numOfNetworkTemplates;
	}


	public List<TradeNetwork> getNetworkList() {
		return networkList;
	}
	
	
	private IStrategy createStrategy(Class strategyClass) throws InstantiationException, IllegalAccessException{
		return (IStrategy)strategyClass.newInstance();
	}
	
	private ITrader createTrader(TradeNetwork network, Class traderClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		return (ITrader)traderClass.getConstructor(IPositionManager.class).newInstance(network.getPosManager());
	}
	
	
	private TradeNetwork populateNetwork(Element networkNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		TradeNetwork network = new TradeNetwork();
		
		network.setNetworkName(networkNode.getAttribute("name").getValue());
		
		network.init(paramMap);
		
		populateComponents(network, networkNode.getChild("components"));
		
		populateInitializations(network, networkNode.getChild("initializations"));
		
		populateConnections(network, networkNode.getChild("connections"));
		return network;
	}


	
	
	
	private void populateComponents(TradeNetwork network, Element connections) throws ClassNotFoundException{
		Iterator itr = connections.getChildren().iterator();
		
		 while (itr.hasNext()) {
           Element elem = (Element) itr.next();
 
           network.addComponent(elem.getAttribute("name").getValue(), Class.forName(elem.getAttribute("class").getValue()));
          }
		 
		 
	}
	
	
	private void populateConnections(TradeNetwork network, Element components) throws ClassNotFoundException{
		Iterator itr = components.getChildren().iterator();
		
		 while (itr.hasNext()) {
           Element elem = (Element) itr.next();
           
           Element fromElement = elem.getChild("from");
           Element toElement = elem.getChild("to");
           
           String fromComponent = fromElement.getAttribute("component").getValue();
           String fromPort = fromElement.getAttribute("port").getValue();
           Integer fromPortNo = fromElement.getAttribute("no")!=null?Integer.valueOf(fromElement.getAttribute("no").getValue()):null;
           
           
           String toComponent = toElement.getAttribute("component").getValue();
           String toPort = toElement.getAttribute("port").getValue();
           Integer toPortNo = toElement.getAttribute("no")!=null?Integer.valueOf(toElement.getAttribute("no").getValue()):null;
           
           network.addConnection(fromComponent, fromPort, fromPortNo, toComponent, toPort, toPortNo);
           
           if(elem.getAttribute("indicator")!=null){
        	   network.getStrategy().addIndicator(elem.getAttribute("indicator").getValue(), toPortNo);
           }
           
          }
		 
		 
	}
	
	
	
	private void populateInitializations(TradeNetwork network,
			Element components) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException {
		Iterator itr = components.getChildren().iterator();

		while (itr.hasNext()) {
			Element elem = (Element) itr.next();

			String componentName = elem.getAttribute("component").getValue();
			String portName = elem.getAttribute("port").getValue();

			Element param = elem.getChild("parameter");
			String value = param.getAttribute("value").getValue();
			String paramType = param.getAttribute("type").getValue();

			if (!(paramType.equalsIgnoreCase("Static")
					|| paramType.equalsIgnoreCase("Configuration")
					|| paramType.equalsIgnoreCase("Strategy") || paramType
						.equalsIgnoreCase("Trader"))) {
				Parameter paramValue = new Parameter("paramname", paramType,
						value);

				network.addInitialization(paramValue.getValue(), componentName,
						portName);
			} else if (paramType.equalsIgnoreCase("Configuration")) {
				network.addInitialization(paramMap.get(value).getValue(),
						componentName, portName);

			} else if (paramType.equalsIgnoreCase("Static")) {
				if (value.equalsIgnoreCase("posManager")) {
					network.addInitialization(network.getPosManager(),
							componentName, portName);
				} else if (value.equalsIgnoreCase("dataSet")) {
					network.addInitialization(network.getDataSet(),
							componentName, portName);
				}
			} else if (paramType.equalsIgnoreCase("Strategy")) {

				network.setStrategy(createStrategy(Class.forName(value)));
				
				network.addInitialization(network.getStrategy(),
						componentName, portName);
				
				
			} else if (paramType.equalsIgnoreCase("Trader")) {
				network.setTrader(createTrader(network, Class.forName(value)));
				
				network.addInitialization(network.getTrader(),
						componentName, portName);
			}

		}

	}
	
	private void populateNetworkList(Element networkNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		
		numOfNetworkTemplates = 0;
		Iterator itr = networkNode.getChildren().iterator();
		
		 while (itr.hasNext()) {
            Element elem = (Element) itr.next();
 
            for(int i=0;i<populationSize;i++)
            	networkList.add(populateNetwork(elem));
			
			numOfNetworkTemplates++;
         }
		
		 
	}
	
	public HashMap<String,List<TradeNetwork>> populateNetworkListBySize(Integer PopSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		Element networksNode = doc.getRootElement().getChildren("networks").get(0);
		
		HashMap<String,List<TradeNetwork>> newProp = new HashMap<String,List<TradeNetwork>>();
		
		Iterator itr = networksNode.getChildren().iterator();
		
		 while (itr.hasNext()) {
            Element elem = (Element) itr.next();
 
            for(int i=0;i<PopSize;i++){
            	TradeNetwork network = populateNetwork(elem);
            	List<TradeNetwork> networkList = newProp.get(network.getNetworkName());
            	
            	if(networkList==null){
            		networkList = new ArrayList<TradeNetwork>();
            	}
            	
            	networkList.add(network);
            	
            	newProp.put(network.getNetworkName(), networkList);
				
            }
         }
		
		 return newProp;
	}
	
	public Integer getPopulationSize() {
		return populationSize;
	}


	public void runNetworks() throws Exception{
		for(TradeNetwork network:networkList){
			network.go();
		}
	}
	
	public NetworkTemplateGenerator(String fileName, HashMap<String, Parameter> paramMap, Integer populationSize) {
		this.paramMap = paramMap;
		this.populationSize = populationSize;
		networkList = new ArrayList<TradeNetwork>();
		SAXBuilder builder = new SAXBuilder();
		
        try {
			doc = builder.build(fileName);
		
			Element networks = doc.getRootElement().getChildren("networks").get(0);
			populateNetworkList(networks);

			
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
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
