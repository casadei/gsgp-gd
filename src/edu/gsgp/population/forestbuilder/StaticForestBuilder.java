package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.terminals.Constant;
import edu.gsgp.utils.MersenneTwister;
import org.apache.commons.math3.analysis.function.Logit;

/**
 * Created by casadei on 20/10/17.
 */
public class StaticForestBuilder extends ForestBuilder {
    private Node[] trees;
    public final static double PRECISION = 0.01;
    public final static int RANGE = 12;


    public StaticForestBuilder(PropertiesManager propertiesManager) {
        super(propertiesManager);
    }

    @Override
    public void initialize(MersenneTwister randomGenerator) {
        initialize((int)(RANGE / PRECISION), randomGenerator);
    }

    @Override
    public void initialize(int size, MersenneTwister randomGenerator) {
        this.size = size;
        this.trees = new Node[size];
        double start = RANGE / -2.0;

        for (int i = 0; i < size; i++) {
            trees[i] = new Constant(start + i * PRECISION);
        }
    }

    @Override
    public Node getRandomTree(MersenneTwister randomGenerator) {
        this.numberOfRequestedTrees++;
        return trees[randomGenerator.nextInt(this.trees.length)];
    }

    @Override
    public double getUniquenessRate() {
        return 1.0;
    }

    @Override
    public ForestBuilder softClone() {
        return new StaticForestBuilder(propertiesManager);
    }

    @Override
    public void dispose() {
        this.trees = null;
    }
}
