package edu.gsgp.population.forestbuilder;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.nodes.Node;
import edu.gsgp.normalization.NormalizationStrategy;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

import java.util.Arrays;

/**
 * Created by casadei on 28/09/17.
 */
public abstract class ForestBuilder {
    protected PropertiesManager propertiesManager;
    protected int size = 0;
    protected int numberOfRequestedTrees = 0;

    public ForestBuilder(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    public abstract void initialize(MersenneTwister randomGenerator);
    public abstract void initialize(int size, MersenneTwister randomGenerator);

    public abstract Node getRandomTree(MersenneTwister randomGenerator);

    public int getSize() {
        return this.size;
    }

    public int getNumberOfRequestedTrees() {
        return this.numberOfRequestedTrees;
    }

    public abstract double getUniquenessRate();
    public abstract ForestBuilder softClone();
    public abstract void dispose();

    protected int getKey(Node tree, ExperimentalData experimentalData) {
        return getKey(evaluate(tree, experimentalData));
    }

    protected int getKey(Fitness fitness) {
        return Arrays.toString(fitness.getSemantics(Utils.DatasetType.TRAINING)).hashCode();
    }

    protected Fitness evaluate(Node tree, ExperimentalData experimentalData){
        Utils.DatasetType dataType = Utils.DatasetType.TRAINING;

        Fitness fitnessFunction = propertiesManager.geFitnessFunction();

        fitnessFunction.resetFitness(dataType, experimentalData);
        Dataset dataset = experimentalData.getDataset(dataType);
        NormalizationStrategy normalizer = propertiesManager.getNormalizationStrategy();
        normalizer.setup(dataset, tree);

        int instanceIndex = 0;
        for (Instance instance : dataset) {
            double estimated = normalizer.normalize(instance);
            fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
        }

        fitnessFunction.computeFitness(dataType);
        return fitnessFunction;
    }

    protected Boolean isConstant(Fitness fitness) {
        double semantics[] = fitness.getSemantics(Utils.DatasetType.TRAINING);

        for(int i = 1; i < semantics.length; i++){
            if(semantics[i-1] != semantics[i]) {
                return false;
            }
        }

        return true;
    }
}
