/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Statistics {
    
    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        SEMANTICS("outputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        LOADED_PARAMETERS("loadedParams.txt");

        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }
    
    protected ExperimentalData expData;
    
    protected float elapsedTime;
    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;
    
    protected float[] meanMDD;
    protected float[] sdMDD;
    
    private String bestTrainingSemantics;
    private String bestTestSemantics;
        
    protected int currentGeneration;
    // ========================= ADDED FOR GECCO PAPER =========================
//    private ArrayList<int[]> trGeTarget;
//    private ArrayList<int[]> tsGeTarget;
    // =========================================================================
    
    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        meanMDD = new float[numGenerations+1];
        sdMDD = new float[numGenerations+1];
        currentGeneration = 0;
        this.expData = expData;
        
        // ======================= ADDED FOR GECCO PAPER =======================
//        trGeTarget = new ArrayList<>();
//        tsGeTarget = new ArrayList<>();
        // =====================================================================
    }
    
    // ========================= ADDED FOR GECCO PAPER =========================
//    public void storeDristInfo(Population pop){
//        
//        if(currentGeneration > 0 && (currentGeneration-1) % 10 == 0){
//        
//            int[] tsGE = new int[expData.getDataset(Utils.DatasetType.TEST).size()];
//            int[] trGE = new int[expData.getDataset(Utils.DatasetType.TRAINING).size()];
//            for(Individual ind : pop){
//                double[] tsSem = ind.getTestSemantics();
//                double[] trSem = ind.getTrainingSemantics();
//                for(int i = 0; i < tsSem.length; i++){
//                    if(tsSem[i] >= expData.getDataset(Utils.DatasetType.TEST).getOutputs()[i])
//                        tsGE[i]++;
//                }
//                for(int i = 0; i < trSem.length; i++){
//                    if(trSem[i] >= expData.getDataset(Utils.DatasetType.TRAINING).getOutputs()[i])
//                        trGE[i]++;
//                }
//            }
//
//            tsGeTarget.add(tsGE);
//            trGeTarget.add(trGE);
//            
//        }
//    }
    // =========================================================================
    
    /**
     * Update the statistics with information obtained in the end of the generation
     * @param pop Current population
     */
    public void addGenerationStatistic(Population pop){        
        // In order to not contabilize the time elapsed by this method we subtract
        // the time elapsed
        long methodTime = System.nanoTime();

        Individual[] bestOfGen = pop.getBestIndividuals();

        String[] sizes = new String[bestOfGen.length];
        String[] trFitnesses = new String[bestOfGen.length];
        String[] tsFitnesses = new String[bestOfGen.length];

        for (int i = 0; i < bestOfGen.length; i++) {
            sizes[i] = bestOfGen[i].getNumNodesAsString();
            trFitnesses[i] = bestOfGen[i].getTrainingFitnessAsString();
            tsFitnesses[i] = bestOfGen[i].getTestFitnessAsString();
        }

        bestOfGenSize[currentGeneration] = String.join("|", sizes);
        bestOfGenTrFitness[currentGeneration] = String.join("|", trFitnesses);
        bestOfGenTsFitness[currentGeneration] = String.join("|", tsFitnesses);

        System.out.println("Best of Gen " + (currentGeneration + 1) + ": RMSE-TR: " + bestOfGenTrFitness[currentGeneration]);

        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }

    public void finishEvolution(Individual[] bestIndividuals) {
        elapsedTime = System.nanoTime() - elapsedTime;
        // Convert nanosecs to secs
        elapsedTime /= 1000000000;

        String[] trFitness = new String[bestIndividuals.length];
        String[] tsFitness = new String[bestIndividuals.length];
        String[] trSemantics = new String[bestIndividuals.length];
        String[] tsSemantics = new String[bestIndividuals.length];

        for (int i = 0; i < bestIndividuals.length; i++) {
            trFitness[i] = bestIndividuals[i].getTrainingFitnessAsString();
            tsFitness[i] = bestIndividuals[i].getTestFitnessAsString();
            trSemantics[i] = StringUtils.join(bestIndividuals[i].getTrainingSemantics(), ',');
            tsSemantics[i] = StringUtils.join(bestIndividuals[i].getTestSemantics(), ',');
        }

        bestTrainingSemantics = String.join("|", trSemantics);
        bestTestSemantics = String.join("|", tsSemantics);
    }
    
    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case SEMANTICS:
                return bestTrainingSemantics;
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case ELAPSED_TIME:
                return elapsedTime + "";
            default:
                return null;
        }
    }
    
    private String concatenateArray(String[] stringArray){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < stringArray.length-1; i++){
            str.append(stringArray[i] + ",");
        }
        str.append(stringArray[stringArray.length-1]);        
        return str.toString();
    }
    
    private String concatenateFloatArray(float[] floatArray) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < floatArray.length-1; i++){
            str.append(Utils.format(floatArray[i]) + ",");
        }
        str.append(Utils.format(floatArray[floatArray.length-1]));        
        return str.toString();
    }

    public void startClock(){
        elapsedTime = System.nanoTime();
    }
}
