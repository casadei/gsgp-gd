/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.utils.Utils;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Individual {
    protected Node tree;
    protected Fitness fitnessFunction;
    public double distance;
    public int dominanceRank;
    public int dominanceCount;
    public List<Individual> dominanceSet = new ArrayList();

    public Individual(Node tree, Fitness fitnessFunction){
        this.tree = tree;
        this.fitnessFunction = fitnessFunction;
    }
    
    public Individual(Fitness fitnessFunction){
        this(null, fitnessFunction);
    }
    
    public Individual(Node tree, BigInteger numNodes, Fitness fitnessFunction) {
        this(tree, fitnessFunction);
        fitnessFunction.setNumNodes(numNodes);
    }
    
    public Individual(Node tree, int numNodes, Fitness fitnessFunction) {
        this(tree, new BigInteger(numNodes + ""), fitnessFunction);
    }
        
    public Individual(Node tree, Fitness fitnessFunction, ExperimentalData data) {
        this(tree, fitnessFunction);
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node randomSubtree) {
        this.tree = randomSubtree;
    }
        
    public Fitness getFitnessFunction(){
        return fitnessFunction;
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public void setNumNodes(BigInteger numNodes) {
        fitnessFunction.setNumNodes(numNodes);
    }
   
    public void startNumNodes() {
        fitnessFunction.setNumNodes(tree.getNumNodes());
    }

    public void resetDominance() {
        dominanceCount = 0;
        dominanceRank = 0;
        dominanceSet.clear();
    }

    @Override
    public Individual clone(){
        if(tree != null)
            return new Individual(tree.clone(null), fitnessFunction);
        return new Individual(fitnessFunction);
    }
    
    public String toString() {
        return tree.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getNumNodesAsString() {
        return fitnessFunction.getNumNodes().toString();
    }
    
    public String getTrainingFitnessAsString() {
        double[] fitness = fitnessFunction.getTrainingFitness();
        String[] formattedFitness = new String[fitness.length];

        for (int i = 0; i < fitness.length; i++) {
            formattedFitness[i] = Utils.format(fitness[i]);
        }

        return String.join(";", formattedFitness);
    }
    
    public String getTestFitnessAsString() {
        double[] fitness = fitnessFunction.getTestFitness();

        if (fitness == null)
            return Utils.format(0);

        String[] formattedFitness = new String[fitness.length];

        for (int i = 0; i < fitness.length; i++) {
            formattedFitness[i] = Utils.format(fitness[i]);
        }

        return String.join(";", formattedFitness);
    }

    public String getValidationFitnessAsString() {
        double[] fitness = fitnessFunction.getValidationFitness();

        if (fitness == null)
            return Utils.format(0);

        String[] formattedFitness = new String[fitness.length];

        for (int i = 0; i < fitness.length; i++) {
            formattedFitness[i] = Utils.format(fitness[i]);
        }

        return String.join(";", formattedFitness);

    }
    
    public double[] getFitness() {
        return fitnessFunction.getComparableValue();
    }

    public double[] getFitness(Utils.DatasetType datasetType) {
        return fitnessFunction.getFitness(datasetType);
    }
    
    public BigInteger getNumNodes() {
        return fitnessFunction.getNumNodes();
    }

    public double[] getSemantics(Utils.DatasetType datasetType) {
        return datasetType == Utils.DatasetType.TRAINING
                ? getTrainingSemantics()
                : getTestSemantics();
    }

    public double[] getTrainingSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TRAINING);
    }
    
    public double[] getTestSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TEST);
    }

    public double[] getValidationSemantics() { return fitnessFunction.getSemantics(Utils.DatasetType.VALIDATION); }

    public double[] getTrainingFitness() {
        return fitnessFunction.getTrainingFitness();
    }

    public double[] getTestFitness() {
        return fitnessFunction.getTrainingFitness();
    }

    public double[] getValidationFitness() {
        return fitnessFunction.getValidationFitness();
    }
}
