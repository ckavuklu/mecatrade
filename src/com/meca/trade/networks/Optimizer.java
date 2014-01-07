package com.meca.trade.networks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Optimizer {
	
	HashMap<String, Parameter> paramMap;
	HashMap<String, Parameter> geneticParamMap;
	HashMap<String,List<TradeNetwork>> m_population;
	HashMap<String,Double> totalFitness;
	private static Random m_rand = new Random();  
	Integer pop_size, max_iteration,elitizm,tournament_size,individuals_size;
	Double crossover_rate,mutation_rate;
	Mankind mankind = null;
	Document doc = null;
	
	
	public Optimizer(String fileName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	
		
		paramMap = new HashMap<String,Parameter>();
		geneticParamMap = new HashMap<String,Parameter>();
		
		SAXBuilder builder = new SAXBuilder();
		
        try {
			doc = builder.build(fileName);
			
			
			populateParameterList();
			
			populateGeneticParameterList();
			
			elitizm = (Integer)geneticParamMap.get("ELITISM_K").getValue();
			pop_size = (Integer)geneticParamMap.get("POP_SIZE").getValue() + elitizm;
			max_iteration = (Integer)geneticParamMap.get("MAX_ITER").getValue();
			crossover_rate = (Double)geneticParamMap.get("CROSSOVER_RATE").getValue();
			mutation_rate = (Double)geneticParamMap.get("MUTATION_RATE").getValue();
			tournament_size = (Integer)geneticParamMap.get("TOURNAMENT_SIZE").getValue();
			individuals_size = (Integer)geneticParamMap.get("INDIVIDUALS_SIZE").getValue();

			
			mankind = new Mankind("NetworkDefinition.xml",paramMap);
			m_population = mankind.populateRaceIndividuals(pop_size);
			
			populateIndicators(m_population);
			
			for(List<TradeNetwork> race:m_population.values()){
				for(TradeNetwork individual:race){
					individual.randIndicatorParametersAndInitialize();
				}
			}
			
			totalFitness = new HashMap<String,Double>();
			
			for(int i=0;i<m_population.size();i++){
				totalFitness.put((String)m_population.keySet().toArray()[i], 0d);
			}
			
			long currTime = Calendar.getInstance().getTimeInMillis();
			
			evaluate();
			System.out.println("Evaluate Takes : " + String.valueOf(Calendar.getInstance().getTimeInMillis() - currTime));
						
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
	
	
	public void initializeAndAddIndividuals(
			HashMap<String, List<TradeNetwork>> individuals,
			HashMap<String, List<TradeNetwork>> newPop) {
		Set<Entry<String, List<TradeNetwork>>> individualSet = individuals
				.entrySet();
		for (Entry<String, List<TradeNetwork>> individualsEntry : individualSet) {

			for (TradeNetwork network : individualsEntry
					.getValue()) {
				network.initializeByIndicatorParameterValues();
				// add network to new population
				List<TradeNetwork> networkList = newPop
						.get(network.getNetworkName());
				if (networkList == null) {
					networkList = new ArrayList<TradeNetwork>();
				}
				networkList.add(network);
				newPop.put(network.getNetworkName(),
						networkList);

			}
		}
	}

	
	
	public void evaluate() throws Exception{
		
		Set<Entry<String, List<TradeNetwork>>> set = m_population.entrySet();

		for (Entry<String, List<TradeNetwork>> e : set) {
			for (TradeNetwork network : e.getValue()) {

				Double networkFitness = network.evaluate(false);
				
				totalFitness.put(e.getKey(),
						totalFitness.get(e.getKey()) + networkFitness);
			}
		}

	}
	
	private void populateGeneticParameterList() {
		Element geneticConfiguration = doc.getRootElement().getChildren("genetic-configuration").get(0);
		Iterator itr = geneticConfiguration.getChildren().iterator();
		
		 while (itr.hasNext()) {
	            Element elem = (Element) itr.next();
	            String paramName = elem.getAttribute("name").getValue();
	            geneticParamMap.put(paramName,new Parameter(paramName, elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
	         }
		 
	}

	
	private void populateParameterList(){
		Element configuration = doc.getRootElement().getChildren("configuration").get(0);
		Iterator itr = configuration.getChildren().iterator();
		
		 while (itr.hasNext()) {
	            Element elem = (Element) itr.next();
	            String paramName = elem.getAttribute("name").getValue();
	            paramMap.put(paramName,new Parameter(paramName, elem.getAttribute("type").getValue(), elem.getAttribute("value").getValue()));
	         }
	}
	
	private void populateIndicators(HashMap<String,List<TradeNetwork>> mankindMap) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException {
		
		Element indicators = doc.getRootElement().getChildren("indicators").get(0);
		
		Set<Entry<String,List<TradeNetwork>>> set = mankindMap.entrySet();
		
		for(Entry<String,List<TradeNetwork>> e:set){
			for(TradeNetwork network:e.getValue()){
				
				Iterator itr = indicators.getChildren().iterator();

				while (itr.hasNext()) {
					
					
					Element indicator =  (Element) itr.next();
					String networkName = indicator.getAttribute("network").getValue();
					
					if(networkName.equalsIgnoreCase(network.getNetworkName())){
						String componentName = indicator.getAttribute("name").getValue();
						String portName = indicator.getAttribute("port").getValue();
						
						Element interval = indicator.getChild("interval");
						String start = interval.getAttribute("start").getValue();
						String end = interval.getAttribute("end").getValue();
						String paramType = interval.getAttribute("type").getValue();
					
						IndicatorParameter indicatorParam = new IndicatorParameter(networkName,componentName, portName, paramType, start, end);
						
						network.getIndicatorParameterList().add(indicatorParam);
					}
					
				}
			}
		}
		
		
		
	}
	
	
	private void printMankind(HashMap<String,List<TradeNetwork>> mankind){
		
		// get the sorted m_population and sort anyway to guarantee it is sorted 
		Set<Entry<String,List<TradeNetwork>>> mankindSet = mankind.entrySet();
		
		for(Entry<String,List<TradeNetwork>> race:mankindSet){
			System.out.println("Race : " + race.getKey());
			
			for(TradeNetwork individual:race.getValue()){
				System.out.println("\t[" + race.getValue().indexOf(individual) + "] " + individual);
			}
			System.out.println("End Of Race");
			
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
	
	public HashMap<String,TradeNetwork> printBestIndividuals() {
		  
		  
		  	HashMap<String,TradeNetwork> result = new HashMap<String,TradeNetwork>();
			Set<Entry<String,List<TradeNetwork>>> set;
			set = this.m_population.entrySet();
			
			for(Entry<String,List<TradeNetwork>> e:set){
				Collections.sort(e.getValue());
				result.put(e.getKey(), e.getValue().get(e.getValue().size()-1));
				System.out.println("Network: " + e.getKey());
				System.out.println("\tBest Individual: " + e.getValue().get(e.getValue().size()-1));
				System.out.println("\t\tPerformance Report: " + e.getValue().get(e.getValue().size()-1).getReportManager().getGeneratedReport());
			}
			return result;
	        
	    }
	
	
	public void reRunBestIndividuals() throws Exception {

		HashMap<String,List<TradeNetwork>> bestIndividuals = mankind.populateRaceIndividuals(1);
		Set<Entry<String,List<TradeNetwork>>> currentMankindSet = m_population.entrySet();
		
		for(Entry<String,List<TradeNetwork>> race:currentMankindSet){
			Collections.sort(race.getValue());
			
			
			TradeNetwork best = race.getValue().get(race.getValue().size()-(1));
			bestIndividuals.get(best.getNetworkName()).get(0).setIndicatorParameterList(best.getIndicatorParameterList());
			System.out.println("Re-Running Best Child For Network: " +best.getNetworkName());
			bestIndividuals.get(best.getNetworkName()).get(0).initializeByIndicatorParameterValues();
			bestIndividuals.get(best.getNetworkName()).get(0).evaluate(true);
			System.out.println("\t\tPerformance Report: " + bestIndividuals.get(best.getNetworkName()).get(0).getReportManager().getGeneratedReport());	
		}
	
		
    }
	
	
	
	/**
	 * 
	 * Elitizm selects the top best individuals
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public HashMap<String,List<TradeNetwork>> elitizm() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		// create new individuals as the number of elitizm for each race from network definitions 
		HashMap<String,List<TradeNetwork>> bestIndividuals = mankind.populateRaceIndividuals(elitizm);

		// get the sorted m_population and sort anyway to guarantee it is sorted 
		Set<Entry<String,List<TradeNetwork>>> currentMankindSet = m_population.entrySet();
		
		for(Entry<String,List<TradeNetwork>> race:currentMankindSet){
			Collections.sort(race.getValue());
			
			for(int iter=0;iter<elitizm;iter++)
			{
				TradeNetwork best = race.getValue().get(race.getValue().size()-(iter+1));
				bestIndividuals.get(best.getNetworkName()).get(iter).setIndicatorParameterList(best.getIndicatorParameterList());
			}		
		}
		return bestIndividuals;
    }
	
	public void crossover(HashMap<String, List<TradeNetwork>> list) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, CloneNotSupportedException {
		if (m_rand.nextDouble() < crossover_rate) {
			
			// create a temporary IndicatorParameter List to hold current Indicator Values and then set Indicators of current individuals using this 
			HashMap<String,List<List<IndicatorParameter>>> currentIndicatorsMap = new HashMap<String,List<List<IndicatorParameter>>>();
			
			Set<Entry<String,List<TradeNetwork>>> set = list.entrySet();
			
			// fill in the temporary IndicatorParameter List
			for(Entry<String,List<TradeNetwork>> e:set){
				for(TradeNetwork network:e.getValue()){
					List<List<IndicatorParameter>> networkParamsList = currentIndicatorsMap.get(network.getNetworkName());
					
					if (networkParamsList == null) 
							networkParamsList = new ArrayList<List<IndicatorParameter>>();
					
					List<IndicatorParameter> networkParams = new ArrayList<IndicatorParameter>() ;
					
					for(IndicatorParameter p:network.getIndicatorParameterList()){
						networkParams.add(new IndicatorParameter(p));
					}
					
					networkParamsList.add(networkParams);
					
					currentIndicatorsMap.put(network.getNetworkName(), networkParamsList);
				}
			}
			
			
			for(Entry<String,List<TradeNetwork>> e:set){
				
				Integer parameterSize = e.getValue().get(0).getIndicatorParameterList().size();
				
				Integer randPoint = m_rand.nextInt(parameterSize);
				
				
				
				int child = 0;
				for(TradeNetwork network:e.getValue()){
					
					int i;
					for (i=0; i<randPoint; ++i) {
						network.setIndicatorParameterValue(i,(Double)currentIndicatorsMap.get(e.getKey()).get(child).get(i).getValue());
			            
			        }
			        for (; i<parameterSize; ++i) {
			        	
			        	if(child==0)
			        		network.setIndicatorParameterValue(i,(Double)currentIndicatorsMap.get(e.getKey()).get(1).get(i).getValue());
			        	else if(child==1)
			        		network.setIndicatorParameterValue(i,(Double)currentIndicatorsMap.get(e.getKey()).get(0).get(i).getValue());
			        }
			        
			        child++;
				}
				
			}
		}
		
    }
		
	public HashMap<String,List<TradeNetwork>> rouletteWheelSelection() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		HashMap<String,List<TradeNetwork>> networkList = mankind.populateRaceIndividuals(2);
		
		Set<Entry<String,List<TradeNetwork>>> set = m_population.entrySet();
		for(int i=0;i<2;i++){
			for(Entry<String,List<TradeNetwork>> e:set){
				TradeNetwork selected = e.getValue().get(m_rand.nextInt(e.getValue().size()));
				
				networkList.get(e.getKey()).get(i).setIndicatorParameterList(selected.getIndicatorParameterList());
				
			}
		}

		return networkList;
        
    }
	
	
	public void mutateIndividuals(HashMap<String, List<TradeNetwork>> individuals) {
		for (Entry<String, List<TradeNetwork>> individual : individuals
				.entrySet()) {

			// Mutation
			if (m_rand.nextDouble() < mutation_rate) {
				individual.getValue().get(0).mutate();

			}

			if (m_rand.nextDouble() < mutation_rate) {
				individual.getValue().get(1).mutate();
			}

		}
	}
		
	public HashMap<String,List<TradeNetwork>> tournamentSelection() throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, CloneNotSupportedException {
	
		// create new individuals for each race from definitions as many as individualsSize 
		HashMap<String,List<TradeNetwork>> individuals = mankind.populateRaceIndividuals(individuals_size);
		//populateIndicators(individuals);
		
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
					
					while(selectedIndividualsForTournament.get(selected.getNetworkName())!=null && selectedIndividualsForTournament.get(selected.getNetworkName()).contains(selected)){
						selected = race.getValue().get(m_rand.nextInt(race.getValue().size()));
					}
					
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
				//setNetworkIndicatorParameterList(individuals.get(selectedRace.getKey()));
				List<IndicatorParameter> newParamList = selectedRace.getValue().get(tournament_size-1).getIndicatorParameterList();
				
				individuals.get(selectedRace.getKey()).get(individualIndex).getIndicatorParameterList().clear();
				
				for(IndicatorParameter newParam:newParamList){
					individuals.get(selectedRace.getKey()).get(individualIndex).getIndicatorParameterList().add(new IndicatorParameter(newParam));
				}
				
			}
		
		} // end of tournament for the size of individuals
		
		
		return individuals;
        
    }
	

	public static void main(String[] args) throws Exception {
		
		try {

			Optimizer optimizer = new Optimizer("NetworkGeneration.xml");

			HashMap<String, List<TradeNetwork>> individuals = new HashMap<String, List<TradeNetwork>>();
			HashMap<String, List<TradeNetwork>> bestIndividuals = new HashMap<String, List<TradeNetwork>>();
			

			
			int count;
			for (int iter = 0; iter < optimizer.max_iteration; iter++) {
				count = 0;
				
				System.out.println("ITERATION - " + iter);
				
				HashMap<String, List<TradeNetwork>> newPop = new HashMap<String, List<TradeNetwork>>();

				// the list is sorted
				System.out.println("MASTER POPULATION");
				optimizer.printMankind(optimizer.m_population);
				
				
				if (optimizer.elitizm > 0) {
					bestIndividuals = optimizer.elitizm();
					optimizer.initializeAndAddIndividuals(bestIndividuals, newPop);
					count += optimizer.elitizm;
				}
				
				System.out.println("NEW POPULATION AFTER ELITIZM");
				optimizer.printMankind(newPop);

				// build new Population
				while (count < optimizer.pop_size) {

					// Selection
					individuals = optimizer.tournamentSelection();
					
					System.out.println("TOURNAMENT SELECTION INDIVIDUALS");
					optimizer.printMankind(individuals);
					// Crossover
					System.out.println("INDIVIDUALS AFTER CROSSOVER");
					optimizer.crossover(individuals);
					optimizer.printMankind(individuals);

					// Mutate
					
					optimizer.mutateIndividuals(individuals);
					System.out.println("INDIVIDUALS AFTER MUTATION");
					optimizer.printMankind(individuals);
					
					optimizer.initializeAndAddIndividuals(individuals, newPop);
					System.out.println("NEW POPULATION AFTER MUTATION");
					optimizer.printMankind(newPop);
					
					
					count += 2;
				}

				optimizer.m_population = newPop;

				optimizer.evaluate();

				System.out.println("BEST INDIVIDUALS");
				optimizer.printBestIndividuals();
			}

			System.out.println("RESULTING MASTER POPULATION");
			optimizer.printMankind(optimizer.m_population);
			
			System.out.println("BEST INDIVIDUALS of TOTAL EXECUTION");
			optimizer.printBestIndividuals();
			
			System.out.println("RERUN BEST INDIVIDUALS");
			optimizer.reRunBestIndividuals();

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