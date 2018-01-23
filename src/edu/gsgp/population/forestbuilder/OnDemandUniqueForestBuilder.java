package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.ERC;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.utils.MersenneTwister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by casadei on 28/09/17.
 */
public class OnDemandUniqueForestBuilder extends ForestBuilder {
    private Map<Integer, Boolean> uniques = new HashMap<>();

    public OnDemandUniqueForestBuilder(PropertiesManager propertiesManager) {
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

        int attempts = 1;

        Node tree = this.propertiesManager.getRandomTree(randomGenerator);
        Fitness fitness = evaluate(tree, this.propertiesManager.getExperimentalData());
        int key = getKey(fitness);

        Node candidateTree = tree;
        Fitness candidateFitness = fitness;
        int candidateKey = key;

        while (attempts++ <= this.propertiesManager.getMaxInitAttempts()) {
            if (isConstant(candidateFitness)) {
                candidateTree = this.propertiesManager.getRandomTree(randomGenerator);
                candidateFitness = evaluate(candidateTree, this.propertiesManager.getExperimentalData());
                candidateKey = getKey(candidateFitness);
                //attempts = 0;
            } else if (uniques.containsKey(candidateKey)) {
                Function func = propertiesManager.getRandomFunction(randomGenerator);
                func.addNode(tree.clone(func), 0);

                for (int i = 1; i < func.getArity(); i++) {
                    func.addNode(new ERC().softClone(randomGenerator), i);
                }

                candidateTree = func;
                candidateFitness = evaluate(func, this.propertiesManager.getExperimentalData());
                candidateKey = getKey(candidateFitness);
            } else {
                uniques.put(candidateKey, true);
                return candidateTree;
            }
        }

        uniques.putIfAbsent(key, true);
        return tree;
    }

    @Override
    public double getUniquenessRate() {
        return (double)this.uniques.size() / this.getSize();
    }

    @Override
    public ForestBuilder softClone() {
        return new OnDemandUniqueForestBuilder(this.propertiesManager);
    }

    @Override
    public void dispose() {
        this.uniques.clear();
        this.uniques = null;
    };
}
