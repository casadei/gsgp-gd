package edu.gsgp.population.operator.gp;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.utils.Utils;

public abstract class GPBreeder extends Breeder {
    protected GPBreeder(PropertiesManager properties, double probability) {
        super(properties, probability);
    }

    protected Fitness evaluate(Fitness fitnessFunction,
                               Node tree,
                               ExperimentalData expData) {
        boolean bloated = tree.getDepth() > properties.getIndividualMaxDepth();

        for(Utils.DatasetType dataType : Utils.DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData, properties.getNumberOfObjectives());
            Dataset dataset = expData.getDataset(dataType);

            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = bloated ? Double.MAX_VALUE : tree.eval(instance.input);
                fitnessFunction.setSemanticsAtIndex(instance, estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }

        return fitnessFunction;
    }

    protected Node replaceNode(Node source, Node target, Node tree) {
        if (source == null || source.getParent() == null) {
            return target;
        }

        Function parent = (Function)source.getParent();
        int parentArgPosition = source.getParentArgPosition();

        parent.addNode(target, parentArgPosition);
        target.setParent(parent, parentArgPosition);

        return tree;
    }

    protected Individual buildIndividual(Individual parent, Node tree, ExperimentalData data) {
        return new Individual(
                tree,
                tree.getNumNodes(),
                evaluate(parent.getFitnessFunction().softClone(), tree, data)
        );
    }
}
