package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.meca.trade.opt.Individual;

public class Optimizer {
	
	HashMap<String, Parameter> paramMap;
	HashMap<String, Parameter> geneticParamMap;
	HashMap<String,List<TradeNetwork>> m_population;
	HashMap<String,Double> totalFitness;
	private static Random m_rand = new Random();  
	Integer pop_size, max_iteration,elitizm;
	Double crossover_rate,mutation_rate;
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
			
			elitizm = (Integer)geneticParamMap.get("ELITISM_K").getValue();
			pop_size = (Integer)geneticParamMap.get("POP_SIZE").getValue() + elitizm;
			max_iteration = (Integer)geneticParamMap.get("MAX_ITER").getValue();
			crossover_rate = (Double)geneticParamMap.get("CROSSOVER_RATE").getValue();
			mutation_rate = (Double)geneticParamMap.get("MUTATION_RATE").getValue();
			
			
			
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
	
	
	private String printTotalFitness(){
		
		String result = "";
		Set<Entry<String,Double>> set;
		set = this.totalFitness.entrySet();
		for(Entry e:set){
			result += "Network : " + (String) e.getKey() + " Total Fitness : " + (Double)e.getValue() +"\r\n";
			
		}
		return result;
	}
	
	
	public HashMap<String,TradeNetwork> findBestIndividuals() {
		  
		  
		  	HashMap<String,TradeNetwork> result = new HashMap<String,TradeNetwork>();
			Set<Entry<String,List<TradeNetwork>>> set;
			set = this.m_population.entrySet();
			
			for(Entry<String,List<TradeNetwork>> e:set){
				Collections.sort(e.getValue());
				result.put(e.getKey(), e.getValue().get(e.getValue().size()-1));
				System.out.println("Optimizer.findBestIndividuals() Network: " + e.getKey() + " value: " +e.getValue().get(e.getValue().size()-1).getFitnessValue());
			}
			return result;
	        
	    }
	
	
	
	public HashMap<String,List<TradeNetwork>> crossover(HashMap<String, List<TradeNetwork>> list) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		  
		HashMap<String,List<TradeNetwork>> newIndiv = networkTemplates.populateNetworkListBySize(2);
		
		Set<Entry<String,List<TradeNetwork>>> set = newIndiv.entrySet();
		
		for(Entry<String,List<TradeNetwork>> e:set){
			
			Integer parameterSize = indicatorMng.getIndicatorMap().get(e.getKey()).size();
			
			Integer randPoint = m_rand.nextInt(parameterSize);
			
			
			HashMap<String,List<IndicatorParameter>> indicatorMap = indicatorMng.getIndicatorMap();
			int child = 0;
			for(TradeNetwork network:e.getValue()){
				List<IndicatorParameter> indicatorList = indicatorMap.get(network.getNetworkName());
				network.setIndicatorParameterList(indicatorList);
				
				int i;
				for (i=0; i<randPoint; ++i) {
					network.setIndicatorParameterValue(i,list.get(e.getKey()).get(child).getIndicatorParameterValue(i));
		            
		        }
		        for (; i<parameterSize; ++i) {
		        	
		        	if(child==0)
		        		network.setIndicatorParameterValue(i,list.get(e.getKey()).get(1).getIndicatorParameterValue(i));
		        	else if(child==1)
		        		network.setIndicatorParameterValue(i,list.get(e.getKey()).get(0).getIndicatorParameterValue(i));
		        }
		        
		        child++;
			}
			
		}
		
		
		
		for(Entry<String,List<TradeNetwork>> e:set){
			for(TradeNetwork network:e.getValue()){
				network.initializeByIndicatorParameterValues();
			}
		}
		
		
		return newIndiv;
		
    }
	
	
	
	
	public HashMap<String,TradeNetwork> rouletteWheelSelection() {
		  
		  
	  	HashMap<String,TradeNetwork> result = new HashMap<String,TradeNetwork>();
		Set<Entry<String,List<TradeNetwork>>> set;
		set = this.m_population.entrySet();
		
		for(Entry<String,List<TradeNetwork>> e:set){
			result.put(e.getKey(), e.getValue().get(m_rand.nextInt(e.getValue().size())));
		}
		return result;
        
    }
	

	public static void main(String[] args) {
		
		try {
			
			Optimizer optimizer = new Optimizer("NetworkGeneration.xml");
			
			HashMap<String,List<TradeNetwork>> newPop = new HashMap<String,List<TradeNetwork>>();
			HashMap<String,List<TradeNetwork>> individuals = new HashMap<String,List<TradeNetwork>>();

			
			
	        System.out.println("Fitness " );
	        System.out.print(optimizer.printTotalFitness());

	        HashMap<String,TradeNetwork> bestIndividuals = optimizer.findBestIndividuals();
	        
	        
	        Set<Entry<String,List<TradeNetwork>>> set = optimizer.m_population.entrySet();
	        
	        
			
			for(Entry<String,List<TradeNetwork>> e:set){
				
				
				int count;
		        for (int iter = 0; iter < optimizer.max_iteration; iter++) {
		            count = 0;

		            
		            Set<Entry<String,TradeNetwork>> bInidividuals = bestIndividuals.entrySet();
		            // Elitism
		            for (Entry<String,TradeNetwork> individ:bInidividuals) {

		            	TradeNetwork network = individ.getValue();
		            	List<TradeNetwork> networkList = newPop.get(network.getNetworkName());
		            	
		            	if(networkList==null){
		            		networkList = new ArrayList<TradeNetwork>();
		            	}
		            	
		            	networkList.add(network);
		            	newPop.put(network.getNetworkName(),networkList);
		                count++;
		            }

		            // build new Population
		            while (count < optimizer.pop_size) {
		            	
		            	for(int i=0;i<2;i++){
			            	 HashMap<String,TradeNetwork> selections = optimizer.rouletteWheelSelection();
			            	
			            	 Set<Entry<String,TradeNetwork>> selected = selections.entrySet();
					         // Elitism
					         for (Entry<String,TradeNetwork> individ:selected) {
					        	 
					        		 TradeNetwork network = individ.getValue();
						            	List<TradeNetwork> networkList = individuals.get(network.getNetworkName());
						            	
						            	if(networkList==null){
						            		networkList = new ArrayList<TradeNetwork>();
						            	}
						            	
						            	networkList.add(network);
						            	individuals.put(network.getNetworkName(),networkList);
					        	 }
					         }
		            	
		            	 // Crossover
		                if ( m_rand.nextDouble() < optimizer.crossover_rate ) {
		                    optimizer.crossover(individuals);
		                }
		                count += 2;
			            }
		                
		               

		            /*    // Mutation
		                if ( m_rand.nextDouble() < optimizer.mutation_rate ) {
		                    
		              

		                    indiv[0].mutate();

		                }
		                if ( m_rand.nextDouble() < optimizer.mutation_rate ) {

		                	indiv[1].mutate();

		                }

		                newPop[count] = indiv[0];
		                newPop[count+1] = indiv[1];
		                count += 2;
		            }
		            pop.setPopulation(newPop);

		            
		            pop.evaluate();
		            System.out.print("Iter =" +iter +" Total Fitness = " + pop.totalFitness);
		            System.out.println(" Best Fitness = " +
		            		pop.findBestIndividual().getFitnessValue());*/
		            
		   
		            
		        }

			}
	        
			
			
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
