/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import edu.gsgp.experiment.config.PropertiesManager;
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

    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        TRAINING_SEMANTICS("trOutputs.csv"),
        TEST_SEMANTICS("tsOutputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        LOADED_PARAMETERS("loadedParams.txt"),
        GROUPS("groups-%02d.txt"),
        SMART_TRAINING_SEMANTICS("smart_tr_outputs.csv"),
        SMART_TRAINING_FEEDBACK("smart_tr_feedback.csv"),
        SMART_TRAINING_FITNESS("smart_tr_fitness.csv"),
        SMART_TRAINING_SANITY("smart_tr_sanity.csv"),
        SMART_TEST_SEMANTICS("smart_ts_outputs.csv"),
        SMART_TEST_FEEDBACK("smart_ts_feedback.csv"),
        SMART_TEST_FITNESS("smart_ts_fitness.csv"),
        SMART_TEST_SANITY("smart_ts_sanity.csv");

        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }

    protected PropertiesManager properties;
    
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
    protected String smartTrFeedback;
    protected String smartTrSanity;

    protected String smartTsSemantics;
    protected String smartTsFitness;
    protected String smartTsFeedback;
    protected String smartTsSanity;

    protected int currentGeneration;
    // ========================= ADDED FOR GECCO PAPER =========================
//    private ArrayList<int[]> trGeTarget;
//    private ArrayList<int[]> tsGeTarget;
    // =========================================================================
    
    public Statistics(PropertiesManager properties, ExperimentalData expData) {
        this.properties = properties;

        int numGenerations = properties.getNumGenerations();

        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        meanMDD = new float[numGenerations+1];
        sdMDD = new float[numGenerations+1];
        currentGeneration = 0;
        this.expData = expData;
    }

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

    private List<Integer> getSampleIndexes(Utils.DatasetType type, int sampleSize ) {
        int testSize = expData.getDataset(type).size();

        List<Integer> available = new ArrayList<>(testSize);

        for (int i = 0; i < testSize; i++) {
            available.add(i);
        }

        Collections.shuffle(available);

        return available.subList(0, sampleSize);
    }

    private Map<Individual, FitnessRMSE> computeValidationFitness(
            Utils.DatasetType datasetType,
            int sampleSize,
            int numberOfObjectives,
            Individual[] bestIndividuals)
    {
        Map<Individual, FitnessRMSE> validationFitness = new HashMap<>();

        for (Individual individual : bestIndividuals) {
            FitnessRMSE fitness = new FitnessRMSE();
            fitness.resetFitness(datasetType, expData, numberOfObjectives);

            validationFitness.put(individual, fitness);
        }

        int instanceIndex = 0;

        for (int index : getSampleIndexes(datasetType, sampleSize)) {
            Instance instance = expData.getDataset(datasetType).get(index);

            for (Individual individual : bestIndividuals) {
                validationFitness.get(individual).setSemanticsAtIndex(
                    instance,
                    individual.getSemantics(datasetType)[index],
                    instance.output,
                    instanceIndex,
                    datasetType
                );
            }

            instanceIndex++;
        }

        for (FitnessRMSE current : validationFitness.values()) {
            current.computeFitness(datasetType);
        }

        return validationFitness;
    }

    private String computeSanity(Utils.DatasetType type, Individual[] bestIndividuals) {
        String[] sanity = new String[bestIndividuals.length];

        for (int i = 0; i < bestIndividuals.length; i++) {
            Individual individual = bestIndividuals[i];

            Fitness function = new FitnessRMSE(true);
            function.resetFitness(type, expData, 1);
            int instanceIndex = 0;

            for (Instance instance : expData.getDataset(type)) {
                double estimated = type == Utils.DatasetType.TRAINING
                        ? individual.getTrainingSemantics()[instanceIndex]
                        : individual.getTestSemantics()[instanceIndex];

                function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex++, type);
            }

            function.computeFitness(type);

            if (type == Utils.DatasetType.TRAINING)
                sanity[i] = Utils.format( function.getTrainingFitness()[0]);
            else
                sanity[i] = Utils.format(function.getTestFitness()[0]);
        }

        return StringUtils.join(sanity, ',');
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

        int[] feedback = new int[expData.getDataset(Utils.DatasetType.TRAINING).size()];

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
            function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex, Utils.DatasetType.TRAINING);
            feedback[instanceIndex] = bestGroup;
            instanceIndex++;
        }

        function.computeFitness(Utils.DatasetType.TRAINING);

        smartTrFitness = Utils.format(function.getTrainingFitness()[0]);
        smartTrSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TRAINING), ',');
        smartTrFeedback = StringUtils.join(feedback, ',');
        smartTrSanity = computeSanity(Utils.DatasetType.TRAINING, bestIndividuals);
    }

    private void computeSmartTestFitness(int numberOfObjectives, Individual[] bestIndividuals) {
        Map<Integer, Individual> bestByObjective = new HashMap<>();

        int trainingSize = expData.getDataset(Utils.DatasetType.TRAINING).size();
        int testSize = expData.getDataset(Utils.DatasetType.TEST).size();
        int sampleSize;
        Utils.DatasetType validationDataset;

        /*
            If the training size is greater or equal to test size, should use the training dataset to do the validation,
            otherwise should use the test size using a sample not greater than training size

         */

        if (trainingSize >= testSize) {
            validationDataset = Utils.DatasetType.TRAINING;
            sampleSize = (int)(Math.floor(trainingSize) * properties.getValidationSampleSize());

        } else {
            validationDataset = Utils.DatasetType.TEST;
            sampleSize = (int)(Math.floor(testSize) * properties.getValidationSampleSize());

            if (testSize > trainingSize)
                sampleSize = Math.min(trainingSize, sampleSize);
        }


        Map<Individual, FitnessRMSE> validationFitness = computeValidationFitness(
                validationDataset,
                sampleSize,
                numberOfObjectives,
                bestIndividuals
        );

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : bestIndividuals) {
                if (!bestByObjective.containsKey(i) ||
                    validationFitness.get(bestByObjective.get(i)).getFitness(validationDataset)[i] >
                    validationFitness.get(individual).getFitness(validationDataset)[i])
                {
                    bestByObjective.put(i, individual);
                }
            }
        }

        Fitness function = new FitnessRMSE(true);
        function.resetFitness(Utils.DatasetType.TEST, expData, 1);

        int[] feedback = new int[expData.getDataset(Utils.DatasetType.TEST).size()];

        int instanceIndex = 0;
        for (Instance instance : expData.getDataset(Utils.DatasetType.TEST)) {
            int bestGroup = 0;
            Individual best = null;

            // Get the individual with lower fitness among the objectives associated to this instance
            for (int i = 0; i < numberOfObjectives; i++) {
                if (!instance.belongsToGroup(i))
                    continue;

                if (best == null ||
                    validationFitness.get(best).getFitness(validationDataset)[bestGroup] >
                    validationFitness.get(bestByObjective.get(i)).getFitness(validationDataset)[i])
                {
                    bestGroup = i;
                    best = bestByObjective.get(i);
                }
            }

            double estimated = best.getTestSemantics()[instanceIndex];
            function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex, Utils.DatasetType.TEST);
            feedback[instanceIndex] = bestGroup;
            instanceIndex++;
        }

        function.computeFitness(Utils.DatasetType.TEST);

        smartTsFitness = Utils.format(function.getTestFitness()[0]);
        smartTsSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TEST), ',');
        smartTsFeedback = StringUtils.join(feedback, ',');
        smartTsSanity = computeSanity(Utils.DatasetType.TEST, bestIndividuals);
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
            case SMART_TRAINING_FEEDBACK:
                return smartTrFeedback;
            case SMART_TRAINING_SANITY:
                return smartTrSanity;
            case SMART_TEST_FITNESS:
                return smartTsFitness;
            case SMART_TEST_SEMANTICS:
                return smartTsSemantics;
            case SMART_TEST_FEEDBACK:
                return smartTsFeedback;
            case SMART_TEST_SANITY:
                return smartTsSanity;
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
