package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.to.IMarketData;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.IStrategy;
import com.meca.trade.to.ITrader;

public class NewMankind {
	IMarketData marketData;
	
	Document definitionDoc = null;
	
	
	private IStrategy createStrategy(Class strategyClass) throws InstantiationException, IllegalAccessException{
		return (IStrategy)strategyClass.newInstance();
	}
	
	private ITrader createTrader(TradeNetwork network, Class traderClass) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		return (ITrader)traderClass.getConstructor(IPositionManager.class).newInstance(network.getPosManager());
	}
	
	
	private TradeNetwork populateNetwork(Element networkNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		
		TradeNetwork network = new TradeNetwork();
		
		network.setNetworkName(networkNode.getAttribute("name").getValue());
		
		network.init(getNetworkConfigurationParameters(networkNode.getChild("configuration")),marketData);
		
		populateComponents(network, networkNode.getChild("components"));
		
		populateInitializations(network, networkNode.getChild("initializations"));
		
		populateConnections(network, networkNode.getChild("connections"));
		
		populateTraderParameters(network, networkNode.getChild("trade-configuration"));
		
		
		
		return network;
	}

	public void populateDefaultIndicators(
			HashMap<String, List<TradeNetwork>> mankindMap)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			SecurityException, InvocationTargetException, NoSuchMethodException {

		Set<Entry<String, List<TradeNetwork>>> set = mankindMap.entrySet();

		for (Element networks : definitionDoc.getRootElement().getChildren(
				"network")) {
			Element indicators = networks.getChildren("indicators").get(0);

			Iterator itr = indicators.getChildren().iterator();

			while (itr.hasNext()) {
				Element indicator = (Element) itr.next();
				String networkName = networks.getAttributeValue("name");
				String componentName = indicator.getAttribute("name")
						.getValue();

				for (Entry<String, List<TradeNetwork>> e : set) {
					if (networkName.equalsIgnoreCase(e.getValue().get(0)
							.getNetworkName())) {
						String portName = indicator.getAttribute("port")
								.getValue();
						String defaultValue = indicator.getAttribute("default")
								.getValue();
						String optimizedValue = indicator.getAttribute(
								"optimized").getValue();
						String paramType = indicator.getAttribute("type")
								.getValue();

						IndicatorParameter indicatorParam = new IndicatorParameter(
								networkName, componentName, portName,
								paramType, null, null);
						indicatorParam.setValue(paramType.equalsIgnoreCase("Double")?Double.valueOf(defaultValue):"null");
						e.getValue().get(0).getIndicatorParameterList()
								.add(indicatorParam);

					}
				}
			}
		}
		
		for (Entry<String, List<TradeNetwork>> e : set) {
			e.getValue().get(0).initializeByIndicatorParameterValues();
		}

	}
	
	private void populateComponents(TradeNetwork network, Element connections) throws ClassNotFoundException{
		Iterator itr = connections.getChildren().iterator();
		
		 while (itr.hasNext()) {
           Element elem = (Element) itr.next();
 
           network.addComponent(elem.getAttribute("name").getValue(), Class.forName(elem.getAttribute("class").getValue()));
          }
		 
		 
	}
	
	private HashMap<String, Parameter> getNetworkConfigurationParameters(Element components) throws ClassNotFoundException{
		Iterator itr = components.getChildren().iterator();
		HashMap<String, Parameter> strategyParam = new HashMap<String, Parameter>();

		while (itr.hasNext()) {
			Element param =  (Element) itr.next();
			
			String name = param.getAttribute("name").getValue();
			String type = param.getAttribute("type").getValue();
			String value = param.getAttribute("value").getValue();
			
			strategyParam.put(name,new Parameter(name, type, value));
		}
		
		return strategyParam;
	}
	
	private void populateTraderParameters(TradeNetwork network, Element components) throws ClassNotFoundException{

		if (components != null && components.getChildren() != null) {
			
			Iterator itr = components.getChildren().iterator();
			List<Parameter> strategyParam = new ArrayList<Parameter>();

			while (itr.hasNext()) {
				Element param = (Element) itr.next();

				String name = param.getAttribute("name").getValue();
				String type = param.getAttribute("type").getValue();
				String value = param.getAttribute("value").getValue();

				strategyParam.add(new Parameter(name, type, value));
			}

			network.getTrader().setConfiguration(strategyParam);
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
			} /*else if (paramType.equalsIgnoreCase("Configuration")) {
				network.addInitialization(runtimeParamMap.get(value).getValue(),
						componentName, portName);

			} */else if (paramType.equalsIgnoreCase("Static")) {
				if (value.equalsIgnoreCase("posManager")) {
					network.addInitialization(network.getPosManager(),
							componentName, portName);
				} else if (value.equalsIgnoreCase("dataSet")) {
					network.addInitialization(network.getDataSet(),
							componentName, portName);
				} else if (value.equalsIgnoreCase("dataIterator")) {
					network.addInitialization(marketData.getMarketDataIterator(),
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
	
	
	
	public HashMap<String,List<TradeNetwork>> populateRaceIndividuals(Integer PopSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
		Element networksNode = definitionDoc.getRootElement();
		
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
	

	/*public void runNetworks() throws Exception{
		for(TradeNetwork network:networkList){
			network.go();
		}
	}
	*/
	
	public NewMankind(String fileName, IMarketData marketData) {
		this.marketData = marketData;
		
		SAXBuilder builder = new SAXBuilder();
		
        try {
			definitionDoc = builder.build(fileName);
			
			
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Document getDefinitionDoc() {
		return definitionDoc;
	}

	

}
