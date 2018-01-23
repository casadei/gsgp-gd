package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.utils.MersenneTwister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by casadei on 28/09/17.
 */
public class ProbabilityDistributedForestBuilder extends ForestBuilder {
    private Map<Integer, Node> trees;
    private int[] indexes;
    private int currentIndex;

    public ProbabilityDistributedForestBuilder(PropertiesManager propertiesManager) {
        super(propertiesManager);
        this.trees = new HashMap<>();
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

        this.indexes = new int[size];
        for (int i = 0; i < size; i++) {
            Node tree = propertiesManager.getRandomTree(randomGenerator);
            int key = getKey(tree, experimentalData);

            this.trees.putIfAbsent(key, tree);
            this.indexes[i] = key;
        }

        this.currentIndex = 0;
    }

    @Override
    public Node getRandomTree(MersenneTwister randomGenerator) {
        this.numberOfRequestedTrees++;

        int key = this.indexes[currentIndex];

        this.currentIndex = ++this.currentIndex % this.indexes.length;

        return this.trees.get(key);
    }

    @Override
    public double getUniquenessRate() {
        return (double)this.trees.size() / this.getSize();
    }

    @Override
    public ForestBuilder softClone() {
        return new ProbabilityDistributedForestBuilder(this.propertiesManager);
    }

    @Override
    public void dispose() {
        this.trees.clear();
        this.indexes = new int[0];
    };
}
