package com.meca.trade.opt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.networks.TradeNetwork;

public class Optimizer {
	List<Parameter> paramList;
	
	private void populateParameterList(Element configuration){
		Iterator itr = configuration.getChildren().iterator();
		
		 while (itr.hasNext()) {
	            Element elem = (Element) itr.next();
	            paramList.add(new Parameter(elem.getAttribute("name").getValue(), elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
	         }
		
		 /*for(Parameter par:paramList){
			 System.out.println("Param " + par.getValue());
		 }*/
	}
	
	
	private void populateNetworkList(Element networkNode){
		TradeNetwork network = new TradeNetwork();
		
		Iterator itr = networkNode.getChildren().iterator();
		
		 while (itr.hasNext()) {
            Element elem = (Element) itr.next();
            network.setNetworkName(elem.getAttribute("name").getValue());
            //paramList.add(new Parameter(elem.getAttribute("name").getValue(), elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
         }
		
		 
	}
	
	public Optimizer(String fileName) {
		paramList = new ArrayList<Parameter>();
		SAXBuilder builder = new SAXBuilder();
		
        try {
			Document doc = builder.build(fileName);
			
		
			Element configuration = doc.getRootElement().getChildren("configuration").get(0);
			populateParameterList(configuration);
			Element networks = doc.getRootElement().getChildren("networks").get(0);
			populateNetworkList(networks);
			
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		
		Optimizer opt = new Optimizer("Network.xml");
		
	}

}
