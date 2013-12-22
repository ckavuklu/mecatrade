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
	Integer pop_size, max_iteration,elitizm,tournament_size,individuals_size;
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
			tournament_size = (Integer)geneticParamMap.get("TOURNAMENT_SIZE").getValue();
			individuals_size = (Integer)geneticParamMap.get("INDIVIDUALS_SIZE").getValue();

			
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
				}
				
				fromIndex++;
			}
			
			evaluate();
						
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
	
	public void evaluate() throws Exception{
		
		Set<Entry<String,List<TradeNetwork>>> set = m_population.entrySet();
		
		for(Entry<String,List<TradeNetwork>> e:set){
			for(TradeNetwork network:e.getValue()){
				
				Double networkFitness = network.evaluate();
				
				totalFitness.put(e.getKey(), totalFitness.get(e.getKey())+ networkFitness);
			}
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
	
	public void crossover(HashMap<String, List<TradeNetwork>> list) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		  
		//HashMap<String,List<TradeNetwork>> newIndiv = networkTemplates.populateNetworkListBySize(2);
		
		Set<Entry<String,List<TradeNetwork>>> set = list.entrySet();
		
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
		
    }
		
	public HashMap<String,List<TradeNetwork>> rouletteWheelSelection() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		HashMap<String,List<TradeNetwork>> networkList = networkTemplates.populateNetworkListBySize(2);
		
		Set<Entry<String,List<TradeNetwork>>> set = m_population.entrySet();
		for(int i=0;i<2;i++){
			for(Entry<String,List<TradeNetwork>> e:set){
				TradeNetwork selected = e.getValue().get(m_rand.nextInt(e.getValue().size()));
				
				networkList.get(e.getKey()).get(i).setIndicatorParameterList(selected.getIndicatorParameterList());
				
			}
		}

		return networkList;
        
    }
		
	public HashMap<String,List<TradeNetwork>> tournamentSelection() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	
		// create new individuals for each race from definitions as many as individualsSize 
		HashMap<String,List<TradeNetwork>> individuals = networkTemplates.populateNetworkListBySize(individuals_size);
		
		// create a race to hold selected individuals for tournament 
		HashMap<String,List<TradeNetwork>> selectedIndividualsForTournament = new HashMap<String,List<TradeNetwork>>();

		Set<Entry<String,List<TradeNetwork>>> currentMankindSet = m_population.entrySet();
		
		// run tournament for the size of individuals needed to be selected
		for(int individualIndex=0;individualIndex<individuals_size;individualIndex++)
		{
			// select individuals for the tournament from each race as the number of tournamentSize and them to selectedIndividualsForTournament
			for(int i=0;i<tournament_size;i++){
				for(Entry<String,List<TradeNetwork>> race:currentMankindSet){
					TradeNetwork selected = race.getValue().get(m_rand.nextInt(race.getValue().size()));
					List<TradeNetwork> networkList = selectedIndividualsForTournament.get(selected.getNetworkName());
	            	if(networkList==null){
	            		networkList = new ArrayList<TradeNetwork>();
	            	}
	            	networkList.add(selected);
	            	selectedIndividualsForTournament.put(selected.getNetworkName(),networkList);
				}
			}
			
			// get the entrySet to sort selected individuals 
			Set<Entry<String,List<TradeNetwork>>> selectedSet = selectedIndividualsForTournament.entrySet();
			
			// sort each race and get Indicators from the one with highest fitness and set Indicators for the individual 
			for(Entry<String,List<TradeNetwork>> selectedRace:selectedSet){
				Collections.sort(selectedRace.getValue());
				individuals.get(selectedRace.getKey()).get(individualIndex).setIndicatorParameterList(selectedRace.getValue().get(tournament_size-1).getIndicatorParameterList());
			
			}
		
		} // end of tournament for the size of individuals
		
		return individuals;
        
    }
	

	public static void main(String[] args) throws Exception {
		
		try {
			
			Optimizer optimizer = new Optimizer("NetworkGeneration.xml");
			
			HashMap<String,List<TradeNetwork>> newPop = new HashMap<String,List<TradeNetwork>>();
			HashMap<String,List<TradeNetwork>> individuals = new HashMap<String,List<TradeNetwork>>();

			
			
	        System.out.println("Fitness " );
	        System.out.print(optimizer.printTotalFitness());

	        HashMap<String,TradeNetwork> bestIndividuals; 
	        
	        
	      //Set<Entry<String,List<TradeNetwork>>> set = optimizer.m_population.entrySet();
		  //for(Entry<String,List<TradeNetwork>> e:set){
				
				
				int count;
		        for (int iter = 0; iter < optimizer.max_iteration; iter++) {
		            count = 0;
		            
		            bestIndividuals = optimizer.findBestIndividuals();
		            
		            Set<Entry<String,TradeNetwork>> bInidividuals = bestIndividuals.entrySet();
		           
		            // Elitism  TODO: to be implemented... 
		            for (int i=0; i<optimizer.elitizm; ++i) {
			            for (Entry<String,TradeNetwork> individ:bInidividuals) {
	
			            	TradeNetwork network = individ.getValue();
			            	List<TradeNetwork> networkList = newPop.get(network.getNetworkName());
			            	
			            	if(networkList==null){
			            		networkList = new ArrayList<TradeNetwork>();
			            	}
			            	
			            	networkList.add(network);
			            	newPop.put(network.getNetworkName(),networkList);
			                
			            }
		            count++;
		            }
		            
		            
		            
		            // build new Population
		            while (count < optimizer.pop_size) {
		            	
		            	// individuals = optimizer.rouletteWheelSelection();
		            	individuals = optimizer.tournamentSelection();
		            	
		            	 // Crossover
		                if ( m_rand.nextDouble() < optimizer.crossover_rate ) {
		                	optimizer.crossover(individuals);
		                }
		                
		                
		                Set<Entry<String,List<TradeNetwork>>> individualSet = individuals.entrySet();
		                
		                for(Entry<String,List<TradeNetwork>> individual:individualSet){
		                	
		                	// Mutation
			                if ( m_rand.nextDouble() < optimizer.mutation_rate ) {
			                	individual.getValue().get(0).mutate();

			                }
			                
			                if ( m_rand.nextDouble() < optimizer.mutation_rate ) {
			                	individual.getValue().get(1).mutate();
			                }
			                
			                for(TradeNetwork eachNetwork:individual.getValue()){
			                	eachNetwork.initializeByIndicatorParameterValues();
			                	
			                	// add network to new population
				            	List<TradeNetwork> networkList = newPop.get(eachNetwork.getNetworkName());
				            	if(networkList==null){
				            		networkList = new ArrayList<TradeNetwork>();
				            	}
				            	networkList.add(eachNetwork);
				            	newPop.put(eachNetwork.getNetworkName(),networkList);
			                	
			        		}
			                
			                // newPop.put(individual.getKey(), individual.getValue());
			                
		                }
		                
		                count += 2;
			            }
		                
		            optimizer.m_population =  newPop;

		            optimizer.evaluate();
		            
		            //optimizer.findBestIndividuals();
		            
		            System.out.println("Optimizer count:" + count + " iter:" + iter + " " + optimizer.printTotalFitness());
		           
		        }
		//}

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
