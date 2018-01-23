package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.utils.MersenneTwister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by casadei on 28/09/17.
 */
public class OnDemandForestBuilder extends ForestBuilder {
    private Map<Integer, Boolean> uniques = new HashMap<>();

    public OnDemandForestBuilder(PropertiesManager propertiesManager) {
        super(propertiesManager);
    }

    @Override
    public void initialize(MersenneTwister randomGenerator) { }

    @Override
    public void initialize(int size, MersenneTwister randomGenerator) { }

    @Override
    public Node getRandomTree(MersenneTwister randomGenerator) {
        this.size++;
        this.numberOfRequestedTrees++;

        Node tree = this.propertiesManager.getRandomTree(randomGenerator);

        uniques.putIfAbsent(getKey(tree, this.propertiesManager.getExperimentalData()), true);

        return tree;
    }

    @Override
    public double getUniquenessRate() {
        return (double)this.uniques.size() / this.getSize();
    }

    @Override
    public ForestBuilder softClone() {
        return new OnDemandForestBuilder(this.propertiesManager);
    }

    @Override
    public void dispose() {
        this.uniques.clear();
        this.uniques = null;
    };
}
