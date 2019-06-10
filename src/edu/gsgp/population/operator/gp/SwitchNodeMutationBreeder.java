package edu.gsgp.population.operator.gp;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.population.Individual;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

public class SwitchNodeMutationBreeder extends GPBreeder {
    public SwitchNodeMutationBreeder(PropertiesManager propertiesManager, Double probability) {
        super(propertiesManager, probability);
    }

    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new UniformCrossoverBreeder(properties, probability);
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        Individual p1 = properties.selectIndividual(originalPopulation, rndGenerator);

        Node newTree = p1.getTree().clone(null);
        Node source = Utils.getRandomNodeFromTree(newTree, rndGenerator);
        Node target;

        if (source instanceof Function) {
            target = properties.getRandomTerminal(rndGenerator);
        } else {
            target = properties.getRandomFunction(rndGenerator);
        }

        return controlBloat(p1, replaceNode(source, target, newTree), expData);
    }
}
