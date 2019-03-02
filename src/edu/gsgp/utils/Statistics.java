/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.population.fitness.FitnessRMSE;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Statistics {

    public static double VALIDATION_SAMPLE_PERCENTAGE = 0.10;

    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        TRAINING_SEMANTICS("trOutputs.csv"),
        TEST_SEMANTICS("tsOutputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        LOADED_PARAMETERS("loadedParams.txt"),
        GROUPS("groups-%02d.txt"),
        SMART_TRAINING_SEMANTICS("smart_training_outputs.csv"),
        SMART_TRAINING_FITNESS("smart_training_fitness.csv"),
        SMART_TEST_SEMANTICS("smart_test_outputs.csv"),
        SMART_TEST_FITNESS("smart_test_fitness.csv");

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
    
    protected String bestTrainingSemantics;
    protected String bestTestSemantics;

    protected String smartTrSemantics;
    protected String smartTrFitness;

    protected String smartTsSemantics;
    protected String smartTsFitness;

        
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

        System.out.println("Best of Gen " + (currentGeneration) + ": RMSE-TR: " + bestOfGenTrFitness[currentGeneration]);

        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }

    public void finishEvolution(int numberOfObjectives, Individual[] bestIndividuals) {
        elapsedTime = System.nanoTime() - elapsedTime;
        // Convert nanosecs to secs
        elapsedTime /= 1000000000;

        String[] trSemantics = new String[bestIndividuals.length];
        String[] tsSemantics = new String[bestIndividuals.length];

        for (int i = 0; i < bestIndividuals.length; i++) {
            trSemantics[i] = StringUtils.join(bestIndividuals[i].getTrainingSemantics(), ',');
            tsSemantics[i] = StringUtils.join(bestIndividuals[i].getTestSemantics(), ',');
        }

        bestTrainingSemantics = String.join("|", trSemantics);
        bestTestSemantics = String.join("|", tsSemantics);

        computeSmartTrainingFitness(numberOfObjectives, bestIndividuals);
        computeSmartTestFitness(numberOfObjectives, bestIndividuals);
    }

    private List<Integer> getSampleIndexes() {
        int testSize = expData.getDataset(Utils.DatasetType.TEST).size();

        List<Integer> available = new ArrayList<>(testSize);

        for (int i = 0; i < testSize; i++) {
            available.add(i);
        }

        Collections.shuffle(available);

        return available.subList(0, (int)(Math.floor(testSize) * VALIDATION_SAMPLE_PERCENTAGE));
    }

    private Map<Individual, FitnessRMSE> computeValidationFitness(int numberOfObjectives, Individual[] bestIndividuals) {
        Map<Individual, FitnessRMSE> validationFitness = new HashMap<>();

        for (Individual individual : bestIndividuals) {
            FitnessRMSE fitness = new FitnessRMSE();
            fitness.resetFitness(Utils.DatasetType.TEST, expData, numberOfObjectives);

            validationFitness.put(individual, fitness);

        }

        int instanceIndex = 0;
        for (int index : getSampleIndexes()) {
            Instance instance = expData.getDataset(Utils.DatasetType.TEST).get(index);
            for (Individual individual : bestIndividuals) {
                validationFitness.get(individual).setSemanticsAtIndex(
                        instance,
                        individual.getTestSemantics()[index],
                        instance.output, instanceIndex++,
                        Utils.DatasetType.TEST
                );
            }
        }

        for (FitnessRMSE current : validationFitness.values()) {
            current.computeFitness(Utils.DatasetType.TEST);
        }

        return validationFitness;
    }

    private void computeSmartTrainingFitness(int numberOfObjectives, Individual[] bestIndividuals) {
        Map<Integer, Individual> bestByObjective = new HashMap<>();

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : bestIndividuals) {
                if (!bestByObjective.containsKey(i) ||
                     bestByObjective.get(i).getTrainingFitness()[i] > individual.getTrainingFitness()[i])
                {
                    bestByObjective.put(i, individual);
                }
            }
        }

        Fitness function = new FitnessRMSE(true);
        function.resetFitness(Utils.DatasetType.TRAINING, expData, 1);

        int instanceIndex = 0;
        for (Instance instance : expData.getDataset(Utils.DatasetType.TRAINING)) {
            int bestGroup = 0;
            Individual best = null;

            // Get the individual with lower fitness among the objectives associated to this instance
            for (int i = 0; i < numberOfObjectives; i++) {
                if (!instance.belongsToGroup(i))
                    continue;

                if (best == null ||
                        best.getTrainingFitness()[bestGroup] > bestByObjective.get(i).getTrainingFitness()[i])
                {
                    bestGroup = i;
                    best = bestByObjective.get(i);
                }
            }

            double estimated = best.getTrainingSemantics()[instanceIndex];
            function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex++, Utils.DatasetType.TRAINING);
        }

        function.computeFitness(Utils.DatasetType.TRAINING);

        smartTrFitness = Utils.format(function.getTrainingFitness()[0]);
        smartTrSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TRAINING), ',');
    }

    private void computeSmartTestFitness(int numberOfObjectives, Individual[] bestIndividuals) {
        Map<Integer, Individual> bestByObjective = new HashMap<>();

        Map<Individual, FitnessRMSE> validationFitness = computeValidationFitness(numberOfObjectives, bestIndividuals);

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : bestIndividuals) {
                if (!bestByObjective.containsKey(i) ||
                    validationFitness.get(bestByObjective.get(i)).getTestFitness()[i] > validationFitness.get(individual).getTestFitness()[i])
                {
                    bestByObjective.put(i, individual);
                }
            }
        }

        Fitness function = new FitnessRMSE(true);
        function.resetFitness(Utils.DatasetType.TEST, expData, 1);

        int instanceIndex = 0;
        for (Instance instance : expData.getDataset(Utils.DatasetType.TEST)) {
            int bestGroup = 0;
            Individual best = null;

            // Get the individual with lower fitness among the objectives associated to this instance
            for (int i = 0; i < numberOfObjectives; i++) {
                if (!instance.belongsToGroup(i))
                    continue;

                if (best == null ||
                    validationFitness.get(best).getTestFitness()[bestGroup] > validationFitness.get(bestByObjective.get(i)).getTestFitness()[i])
                {
                    bestGroup = i;
                    best = bestByObjective.get(i);
                }
            }

            double estimated = best.getTestSemantics()[instanceIndex];
            function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex++, Utils.DatasetType.TEST);
        }

        function.computeFitness(Utils.DatasetType.TEST);

        smartTsFitness = Utils.format(function.getTestFitness()[0]);
        smartTsSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TEST), ',');
    }
    
    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case TRAINING_SEMANTICS:
                return bestTrainingSemantics;
            case TEST_SEMANTICS:
                return bestTestSemantics;
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case ELAPSED_TIME:
                return elapsedTime + "";
            case SMART_TRAINING_FITNESS:
                return smartTrFitness;
            case SMART_TRAINING_SEMANTICS:
                return smartTrSemantics;
            case SMART_TEST_FITNESS:
                return smartTsFitness;
            case SMART_TEST_SEMANTICS:
                return smartTsSemantics;
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
