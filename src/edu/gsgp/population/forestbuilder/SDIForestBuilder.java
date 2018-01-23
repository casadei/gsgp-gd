package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Terminal;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by casadei on 30/09/17.
 */
public class SDIForestBuilder extends ForestBuilder {
    private Node trees[];
    private Map<Integer, Boolean> semantics;
    private int currentIndex;

    public SDIForestBuilder(PropertiesManager propertiesManager) {
        super(propertiesManager);

        this.semantics = new HashMap<>();
    }

    @Override
    public void initialize(MersenneTwister randomGenerator) {
        this.initialize(propertiesManager.getPopulationSize() * propertiesManager.getNumGenerations(), randomGenerator);
    }

    @Override
    public void initialize(int size, MersenneTwister randomGenerator) {
        this.size = size;

        // Dataset used just to compare semantics
        ExperimentalData experimentalData = this.propertiesManager.getExperimentalData();

        int index = 0;
        this.trees = new Node[size];
        for(Terminal t : propertiesManager.getTerminalSet(randomGenerator)){
            Node tree = t.softClone(randomGenerator);
            Fitness fitnessFunction = evaluate(tree, experimentalData);

            semantics.putIfAbsent(getKey(fitnessFunction), true);
            this.trees[index++] = tree;
        }

        int numAttempts = 0;
        Function candidateFunc = null;

        while (index < size){
            Function func = propertiesManager.getRandomFunction(randomGenerator);

            for (int i = 0; i < func.getArity(); i++){
                func.addNode(this.trees[randomGenerator.nextInt(index)].clone(func), i);
            }

            Fitness fitnessFunction = evaluate(func, experimentalData);

            if (numAttempts == 0){
                candidateFunc = func;
            }

            numAttempts++;

            if (!isConstant(fitnessFunction)) {
                int key = getKey(fitnessFunction);

                if (!this.semantics.containsKey(key)) {
                    this.trees[index++] = func;
                    this.semantics.put(key, true);
                    numAttempts = 0;
                }
            }

            if (numAttempts >= propertiesManager.getMaxInitAttempts()) {
                this.trees[index++] = candidateFunc;
                numAttempts = 0;
            }
        }

        this.currentIndex = 0;
    }

    @Override
    public Node getRandomTree(MersenneTwister randomGenerator) {
        Node tree = this.trees[this.currentIndex];
        this.currentIndex = ++this.currentIndex % this.trees.length;

        return tree;

    }

    @Override
    public double getUniquenessRate() {
        return (double)this.semantics.size() / size;
    }

    @Override
    public ForestBuilder softClone() {
        return new SDIForestBuilder(this.propertiesManager);
    }

    @Override
    public void dispose() {};
}
