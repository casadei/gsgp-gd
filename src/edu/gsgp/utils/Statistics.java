/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import edu.gsgp.experiment.config.PropertiesManager;
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
        STACKING_TRAINING_SEMANTICS("stacking_tr_outputs.csv"),
        STACKING_TRAINING_FEEDBACK("stacking_tr_feedback.csv"),
        STACKING_TRAINING_FITNESS("stacking_tr_fitness.csv"),
        STACKING_TRAINING_SANITY("stacking_tr_sanity.csv"),
        STACKING_TEST_SEMANTICS("stacking_ts_outputs.csv"),
        STACKING_TEST_FEEDBACK("stacking_ts_feedback.csv"),
        STACKING_TEST_FITNESS("stacking_ts_fitness.csv"),
        STACKING_TEST_SANITY("stacking_ts_sanity.csv"),
        STACKING_VAL_FITNESS("stacking_val_fitness.csv"),
        STACKING_VAL_SEMANTICS("stacking_val_semantics.csv"),
        STACKING_VAL_FEEDBACK("stacking_val_feedback.csv"),
        STACKING_VAL_SANITY("stacking_val_sanity.csv"),
        FRONTS_TRAINING_SIZES("fronts_tr_sizes.csv"),
        FRONTS_TRAINING("fronts_tr.csv"),
        DIVERSITY_TRAINING("diversity_tr.csv"),
        NON_DOMINATED_TR_SEMANTICS("non_dominated_tr_semantics.csv"),
        NON_DOMINATED_TS_SEMANTICS("non_dominated_ts_semantics.csv"),
        NON_DOMINATED_VAL_SEMANTICS("non_dominated_val_semantics.csv");

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

    protected String stackingTrSemantics;
    protected String stackingTrFitness;
    protected String stackingTrFeedback;
    protected String stackingTrSanity;

    protected String stackingTsSemantics;
    protected String stackingTsFitness;
    protected String stackingTsFeedback;
    protected String stackingTsSanity;

    protected String frontsTr;
    protected String diversityTr;

    protected List<Individual> nonDominated;
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

        System.out.println("Best of Gen " + (currentGeneration) + ": RMSE-TR: " + bestOfGenTrFitness[currentGeneration]);

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

        frontsTr = StringUtils.join(outerTrFitness, '#');
        diversityTr = StringUtils.join(outerTrDiversity, '#');

        nonDominated = new ArrayList<>();
        nonDominated.addAll(pop.getFronts().get(0));

        computeStackingTrainingFitness(numberOfObjectives, nonDominated);
        computeStackingTestFitness(numberOfObjectives, nonDominated);
    }

    private String computeSanity(Utils.DatasetType type, List<Individual> nonDominated) {
        String[] sanity = new String[nonDominated.size()];

        for (int i = 0; i < nonDominated.size(); i++) {
            Individual individual = nonDominated.get(i);

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

    private void computeStackingTrainingFitness(int numberOfObjectives, List<Individual> nonDominated) {
        Map<Integer, Individual> bestByObjective = new HashMap<>();

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : nonDominated) {
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

        stackingTrFitness = Utils.format(function.getTrainingFitness()[0]);
        stackingTrSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TRAINING), ',');
        stackingTrFeedback = StringUtils.join(feedback, ',');
        stackingTrSanity = computeSanity(Utils.DatasetType.TRAINING, nonDominated);
    }

    private void computeStackingTestFitness(int numberOfObjectives, List<Individual> nonDominated) {
        Map<Integer, Individual> bestByObjective = new HashMap<>();

        // Get best individual by objective according to the validation fitness results
        for (int i = 0; i < numberOfObjectives; i++) {
            for (Individual individual : nonDominated) {
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

        stackingTsFitness = Utils.format(function.getTestFitness()[0]);
        stackingTsSemantics = StringUtils.join(function.getSemantics(Utils.DatasetType.TEST), ',');
        stackingTsFeedback = StringUtils.join(feedback, ',');
        stackingTsSanity = computeSanity(Utils.DatasetType.TEST, nonDominated);
    }

    public String asWritableString(StatsType type) {
        String[] buffer;

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
            case STACKING_TRAINING_FITNESS:
                return stackingTrFitness;
            case STACKING_TRAINING_SEMANTICS:
                return stackingTrSemantics;
            case STACKING_TRAINING_FEEDBACK:
                return stackingTrFeedback;
            case STACKING_TRAINING_SANITY:
                return stackingTrSanity;
            case STACKING_TEST_FITNESS:
                return stackingTsFitness;
            case STACKING_TEST_SEMANTICS:
                return stackingTsSemantics;
            case STACKING_TEST_FEEDBACK:
                return stackingTsFeedback;
            case STACKING_TEST_SANITY:
                return stackingTsSanity;
            case FRONTS_TRAINING_SIZES:
                return concatenateArray(frontsSizesByGen);
            case FRONTS_TRAINING:
                return frontsTr;
            case DIVERSITY_TRAINING:
                return diversityTr;
            case NON_DOMINATED_TR_SEMANTICS:
                buffer = new String[nonDominated.size()];

                for (int i = 0; i < nonDominated.size(); i++) {
                    buffer[i] = StringUtils.join(nonDominated.get(i).getTrainingSemantics(), ';');
                }

                return concatenateArray(buffer);
            case NON_DOMINATED_TS_SEMANTICS:
                buffer = new String[nonDominated.size()];

                for (int i = 0; i < nonDominated.size(); i++) {
                    buffer[i] = StringUtils.join(nonDominated.get(i).getTestSemantics(), ';');
                }

                return concatenateArray(buffer);
            case NON_DOMINATED_VAL_SEMANTICS:
                buffer = new String[nonDominated.size()];

                for (int i = 0; i < nonDominated.size(); i++) {
                    buffer[i] = StringUtils.join(nonDominated.get(i).getValidationSemantics(), ';');
                }

                return concatenateArray(buffer);
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
