/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.experiment.config;

import edu.gsgp.GSGP;
import edu.gsgp.experiment.data.DataProducer;
import edu.gsgp.experiment.data.DataWriter;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.utils.MersenneTwister;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Experimenter {    
    protected PropertiesManager parameters;
    
    protected DataProducer dataProducer;
    
    public Experimenter(String[] args) throws Exception{
        parameters = new PropertiesManager(args);
    }
    
    public void runExperiment(){
        if(parameters.isParameterLoaded()){
            try {
                Experiment experiments[] = new Experiment[parameters.getNumExperiments()];
                int numThreads = Math.min(parameters.getNumThreads(), parameters.getNumExperiments());
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);

                // Run the algorithm for a defined number of repetitions
                for(int execution = 0; execution < parameters.getNumExperiments(); execution++){
                    parameters.updateExperimentalData();
                    ExperimentalData data = parameters.getExperimentalData().softClone();

                    MersenneTwister randomGenerator = parameters.getRandomGenerator();

                    parameters.getClassifier().classify(randomGenerator, data, parameters.getNumberOfObjectives());

                    DataWriter.writeGroups(parameters, data,execution + 1);

                    experiments[execution] = new Experiment(new GSGP(parameters, data, randomGenerator), execution);
                    executor.execute(experiments[execution]);
                }
                executor.shutdown();
                executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                DataWriter.writeLoadedParameters(parameters);
            } 
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class Experiment implements Runnable{
        GSGP gsgpInstance;
        int id;

        public Experiment(GSGP gsgpInstance, int id) {
            this.gsgpInstance = gsgpInstance;
            this.id = id;
        }
        
        private synchronized void writeStatistics() throws Exception{
            DataWriter.writeResults(parameters.getOutputDir(), 
                    parameters.getFilePrefix(), 
                    gsgpInstance.getStatistics(), id);
        }
        
        @Override
        public void run() {
            try{
                gsgpInstance.evolve();
                writeStatistics();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
        
    }
}
