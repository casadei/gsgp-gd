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
        BEST_OF_GEN_VAL_FIT("valFitness.csv"),
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
        SMART_TEST_SANITY("smart_ts_sanity.csv"),
        TRAINING_FRONTS_SIZES("tr_fronts_sizes.csv"),
        TRAINING_FRONTS("tr_fronts.csv"),
        TRAINING_DIVERSITY("tr_diversity.csv"),
        PARETO_FRONTIER_TR_SEMANTICS("pareto_tr_semantics.csv"),
        PARETO_FRONTIER_TS_SEMANTICS("pareto_ts_semantics.csv"),
        PARETO_FRONTIER_VAL_SEMANTICS("pareto_val_semantics.csv");


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
    protected String[] bestOfGenValFitness;
    protected String[] frontsSizesByGen;
    
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

    protected String trFronts;
    protected String trDiversity;

    protected String[] paretoTrSemantics;
    protected String[] paretoTsSemantics;
    protected String[] paretoValSemantics;

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
        bestOfGenValFitness = new String[numGenerations+1];
        frontsSizesByGen = new String[numGenerations+1];
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
        String[] valFitnesses = new String[bestOfGen.length];

        for (int i = 0; i < bestOfGen.length; i++) {
            sizes[i] = bestOfGen[i].getNumNodesAsString();
            trFitnesses[i] = bestOfGen[i].getTrainingFitnessAsString();
            tsFitnesses[i] = bestOfGen[i].getTestFitnessAsString();
            valFitnesses[i] = bestOfGen[i].getValidationFitnessAsString();
        }

        bestOfGenSize[currentGeneration] = String.join("|", sizes);
        bestOfGenTrFitness[currentGeneration] = String.join("|", trFitnesses);
        bestOfGenTsFitness[currentGeneration] = String.join("|", tsFitnesses);
        bestOfGenValFitness[currentGeneration] = String.join("|", valFitnesses);


        List<Integer> frontSizes = new ArrayList<>();

        for (List<Individual> front : pop.getFronts()) {
            frontSizes.add(front.size());
        }

        frontsSizesByGen[currentGeneration] = StringUtils.join(frontSizes, '#');

        System.out.println("Best of Gen " + (currentGeneration) + ": MSE-TR: " + bestOfGenTrFitness[currentGeneration]);

        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }

    public void finishEvolution(int numberOfObjectives, Population pop) {
        elapsedTime = System.nanoTime() - elapsedTime;
        // Convert nanosecs to secs
        elapsedTime /= 1000000000;

        Individual[] bestIndividuals = pop.getBestIndividuals();

        String[] trSemantics = new String[bestIndividuals.length];
        String[] tsSemantics = new String[bestIndividuals.length];

        for (int i = 0; i < bestIndividuals.length; i++) {
            trSemantics[i] = StringUtils.join(bestIndividuals[i].getTrainingSemantics(), ',');
            tsSemantics[i] = StringUtils.join(bestIndividuals[i].getTestSemantics(), ',');
        }

        bestTrainingSemantics = String.join("|", trSemantics);
        bestTestSemantics = String.join("|", tsSemantics);

        List<String> outerTrFitness = new ArrayList<>();
        List<String> outerTrDiversity = new ArrayList<>();

        for (List<Individual> front : pop.getFronts()) {
            List<String> innerTrFitness = new ArrayList<>();
            List<Double> innerTrDiversity = new ArrayList<>();

            for (Individual ind : front) {
                innerTrFitness.add(ind.getTrainingFitnessAsString());
                innerTrDiversity.add(ind.distance);
            }

            outerTrFitness.add(StringUtils.join(innerTrFitness, '|'));
            outerTrDiversity.add(StringUtils.join(innerTrDiversity, '|'));
        }

        trFronts = StringUtils.join(outerTrFitness, '#');
        trDiversity = StringUtils.join(outerTrDiversity, '#');

        computeSmartTrainingFitness(numberOfObjectives, bestIndividuals);
        computeSmartTestFitness(numberOfObjectives, bestIndividuals);

        List<Individual> paretoFrontier = pop.getFronts().get(0);
        paretoTrSemantics = new String[paretoFrontier.size()];
        paretoTsSemantics = new String[paretoFrontier.size()];
        paretoValSemantics = new String[paretoFrontier.size()];

        for (int i = 0; i < paretoFrontier.size(); i++) {
            paretoTrSemantics[i] = StringUtils.join(paretoFrontier.get(i).getTrainingSemantics(), ';');
            paretoTsSemantics[i] = StringUtils.join(paretoFrontier.get(i).getTestSemantics(), ';');
            paretoValSemantics[i] = StringUtils.join(paretoFrontier.get(i).getValidationSemantics(), ';');
        }
    }

    private String computeSanity(Utils.DatasetType type, Individual[] bestIndividuals) {
        String[] sanity = new String[bestIndividuals.length];

        for (int i = 0; i < bestIndividuals.length; i++) {
            Individual individual = bestIndividuals[i];

            Fitness function = new FitnessRMSE(true);
            function.resetFitness(type, expData, 1);
            int instanceIndex = 0;

            for (Instance instance : expData.getDataset(type)) {
                double estimated = individual.getSemantics(type)[instanceIndex];

                function.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex++, type);
            }

            function.computeFitness(type);

            sanity[i] = Utils.format(function.getFitness(type)[0]);
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

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : bestIndividuals) {
                if (!bestByObjective.containsKey(i) ||
                    bestByObjective.get(i).getValidationFitness()[i] >
                    individual.getValidationFitness()[i])
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
                    best.getValidationFitness()[bestGroup] >
                    bestByObjective.get(i).getValidationFitness()[i])
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
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case BEST_OF_GEN_VAL_FIT:
                return concatenateArray(bestOfGenValFitness);
            case TRAINING_SEMANTICS:
                return bestTrainingSemantics;
            case TEST_SEMANTICS:
                return bestTestSemantics;
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
            case TRAINING_FRONTS_SIZES:
                return concatenateArray(frontsSizesByGen);
            case TRAINING_FRONTS:
                return trFronts;
            case TRAINING_DIVERSITY:
                return trDiversity;
            case PARETO_FRONTIER_TR_SEMANTICS:
                return concatenateArray(paretoTrSemantics);
            case PARETO_FRONTIER_TS_SEMANTICS:
                return concatenateArray(paretoTsSemantics);
            case PARETO_FRONTIER_VAL_SEMANTICS:
                return concatenateArray(paretoValSemantics);
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
