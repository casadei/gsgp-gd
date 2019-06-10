package edu.gsgp.population.operator.gp;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.population.Individual;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

public class NodeReplacementMutationBreeder extends GPBreeder {
    public NodeReplacementMutationBreeder(PropertiesManager propertiesManager, Double probability) {
        super(propertiesManager, probability);
    }

    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new NodeReplacementMutationBreeder(properties, probability);
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        Individual p1 = properties.selectIndividual(originalPopulation, rndGenerator);

        Node newTree = p1.getTree().clone(null);
        Node source = Utils.getRandomNodeFromTree(newTree, rndGenerator);

        Node target;

        if (source instanceof Function) {
            target = properties.getRandomFunction(rndGenerator);

            for (int i = 0; i < target.getArity(); i++) {
                source.getChild(i).setParent(target, i);
                ((Function)target).addNode(source.getChild(i), i);
            }
        } else {
            target = properties.getRandomTerminal(rndGenerator);
        }

        return buildIndividual(p1, replaceNode(source, target, newTree), expData);
    }
}
