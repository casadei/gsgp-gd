package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.ERC;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.utils.MersenneTwister;

import java.util.*;

/**
 * Created by casadei on 28/09/17.
 */
public class SmartForestBuilder extends ForestBuilder {
    private Node[] trees;
    private int currentIndex;

    public SmartForestBuilder(PropertiesManager propertiesManager) {
        super(propertiesManager);
    }

    @Override
    public void initialize(MersenneTwister randomGenerator) {
        int size = propertiesManager.getPopulationSize() * propertiesManager.getNumGenerations();
        this.initialize((int)Math.ceil(size * 1.5), randomGenerator);
    }

    @Override
    public void initialize(int size, MersenneTwister randomGenerator) {
        this.size = size;

        // Dataset used just to compare semantics
        ExperimentalData experimentalData = this.propertiesManager.getExperimentalData();
        Map<Integer, Boolean> semantics = new HashMap<>();

        int attempts = 0;

        Node candidateTree = null;
        Node tree = null;

        int index = 0;
        this.trees = new Node[size];
        while (index < size) {
            if (attempts == 0){
                tree = propertiesManager.getRandomTree(randomGenerator);
                candidateTree = tree;
            } else {
                Function func = propertiesManager.getRandomFunction(randomGenerator);
                func.addNode(tree.clone(func), 0);
                for (int i = 1; i < func.getArity(); i++) {
                    func.addNode(new ERC().softClone(randomGenerator), i);
                }

                tree = func;
            }

            Fitness fitness = evaluate(tree, experimentalData);
            attempts++;

            int key = getKey(fitness);

            if (isConstant(fitness)) {
                attempts = 0;
            } else if (!semantics.containsKey(key)) {
                semantics.put(key, true);
                this.trees[index++] = tree;

                attempts = 0;
            }

            if (attempts >= propertiesManager.getMaxInitAttempts()) {
                this.trees[index++] = candidateTree;
                attempts = 0;
            }
        }

        shuffle(randomGenerator);

        this.currentIndex = 0;
    }

    private void shuffle(MersenneTwister randomGenerator) {
        for (int i = this.trees.length - 1; i >= 0; i--) {
            int j = randomGenerator.nextInt(i + 1);

            Node aux = this.trees[i];
            this.trees[i] = this.trees[j];
            this.trees[j] = aux;
        }
    }

    @Override
    public Node getRandomTree(MersenneTwister randomGenerator) {
        this.numberOfRequestedTrees++;

        Node tree = this.trees[this.currentIndex];
        this.currentIndex = ++this.currentIndex % this.trees.length;

        return tree;
    }

    @Override
    public double getUniquenessRate() {
        return (double)this.trees.length / this.getSize();
    }

    @Override
    public ForestBuilder softClone() {
        return new SmartForestBuilder(this.propertiesManager);
    }

    @Override
    public void dispose() {
        this.currentIndex = 0;
        this.trees = new Node[0];
    };
}
