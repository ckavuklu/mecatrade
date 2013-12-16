package com.meca.trade.opt;

import java.util.Random;

public class Population
{
	public static int GENESIZE = 10;

    final static int ELITISM_K = 5;
    final static int POP_SIZE = 200 + ELITISM_K;  // population size
    final static int MAX_ITER = 1000;             // max number of iterations
    final static double MUTATION_RATE = 0.020;     // probability of mutation
    final static double CROSSOVER_RATE = 0.7;     // probability of crossover

    private static Random m_rand = new Random();  // random-number generator
    private Individual[] m_population;
    private double totalFitness;

	
    public static int[] GeneStart = new int[GENESIZE];
    public static int[] GeneEnd = new int[GENESIZE];
    
    
    public Population(int[] START,int[] END) {
        m_population = new Individual[POP_SIZE];

        // init population
        for (int i = 0; i < POP_SIZE; i++) {
            m_population[i] = new Individual(START,END);
            m_population[i].randGenes();
            System.out.println(m_population[i].toString());
        }

        
        // evaluate current population
        this.evaluate();
    }

    public void setPopulation(Individual[] newPop) {
        // this.m_population = newPop;
        System.arraycopy(newPop, 0, this.m_population, 0, POP_SIZE);
    }

    public Individual[] getPopulation() {
        return this.m_population;
    }

    public double evaluate() {
        this.totalFitness = 0.0;
        for (int i = 0; i < POP_SIZE; i++) {
            this.totalFitness += m_population[i].evaluate();
        }
        return this.totalFitness;
    }

    public Individual rouletteWheelSelection() {
        double randNum = m_rand.nextDouble() * this.totalFitness;
        int idx;
        for (idx=0; idx<POP_SIZE && randNum>0; ++idx) {
            randNum -= m_population[idx].getFitnessValue();
        }
        return m_population[idx-1];
    }

    public Individual findBestIndividual() {
        int idxMax = 0, idxMin = 0;
        double currentMax = 0.0;
        double currentMin = 1.0;
        double currentVal;

        for (int idx=0; idx<POP_SIZE; ++idx) {
            currentVal = m_population[idx].getFitnessValue();
            if (currentMax < currentMin) {
                currentMax = currentMin = currentVal;
                idxMax = idxMin = idx;
            }
            if (currentVal > currentMax) {
                currentMax = currentVal;
                idxMax = idx;
            }
            if (currentVal < currentMin) {
                currentMin = currentVal;
                idxMin = idx;
            }
        }
        
        // System.out.println("Best Ind.:" + m_population[idxMax].toString() + " Index: " + idxMax);

        //return m_population[idxMin];      // minimization
        return m_population[idxMax];        // maximization
        
        
    }

    public static Individual[] crossover(Individual indiv1,Individual indiv2) {
        Individual[] newIndiv = new Individual[2];
        newIndiv[0] = new Individual(GeneStart,GeneEnd);
        newIndiv[1] = new Individual(GeneStart,GeneEnd);

        int randPoint = m_rand.nextInt(Individual.SIZE);
        int i;
        for (i=0; i<randPoint; ++i) {
            newIndiv[0].setGene(i, indiv1.getGene(i));
            newIndiv[1].setGene(i, indiv2.getGene(i));
        }
        for (; i<Individual.SIZE; ++i) {
            newIndiv[0].setGene(i, indiv2.getGene(i));
            newIndiv[1].setGene(i, indiv1.getGene(i));
        }

        return newIndiv;
    }


    public static void main(String[] args) {

    	
    	
    	// set interval for Genes 
    
    	for (int iter = 0; iter < GENESIZE; iter++) {
    		GeneStart[iter] = 10*iter;
    		GeneEnd[iter] = 10*iter+10;
    	 }
    	
    	        	
        Population pop = new Population(GeneStart,GeneEnd);
 
        Individual[] newPop = new Individual[POP_SIZE];
        Individual[] indiv = new Individual[2];
      
        // current population
        System.out.print("Total Fitness = " + pop.totalFitness);
        System.out.println(" Best Fitness = " + 
            pop.findBestIndividual().getFitnessValue());

//        System.out.println("Best Individual " +  pop.findBestIndividual().toString());
        
        // main loop
        int count;
        for (int iter = 0; iter < MAX_ITER; iter++) {
            count = 0;

            // Elitism
            for (int i=0; i<ELITISM_K; ++i) {
                newPop[count] = pop.findBestIndividual();
                count++;
            }

            // build new Population
            while (count < POP_SIZE) {
                // Selection
                indiv[0] = pop.rouletteWheelSelection();
                indiv[1] = pop.rouletteWheelSelection();

                // Crossover
                if ( m_rand.nextDouble() < CROSSOVER_RATE ) {
                    indiv = crossover(indiv[0], indiv[1]);
                }

                // Mutation
                if ( m_rand.nextDouble() < MUTATION_RATE ) {
                    
               //     System.out.println("Before Mutation :" + indiv[0].toString());

                    indiv[0].mutate();

               //     System.out.println("After Mutation :" + indiv[0].toString());

                }
                if ( m_rand.nextDouble() < MUTATION_RATE ) {

                 //   System.out.println("Before Mutation :" + indiv[1].toString());

                	indiv[1].mutate();

                //	System.out.println("After Mutation :" + indiv[1].toString());

                }

                // add to new population
                newPop[count] = indiv[0];
                newPop[count+1] = indiv[1];
                count += 2;
            }
            pop.setPopulation(newPop);

            // reevaluate current population
            pop.evaluate();
            System.out.print("Iter =" +iter +" Total Fitness = " + pop.totalFitness);
            System.out.println(" Best Fitness = " +
            		pop.findBestIndividual().getFitnessValue());
            
    //        System.out.println("Best Individual " +  pop.findBestIndividual().toString());
            
        }

        // best indiv
        Individual bestIndiv = pop.findBestIndividual();
    }
        
}
