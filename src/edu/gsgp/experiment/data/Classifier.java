package edu.gsgp.experiment.data;

import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.utils.MersenneTwister;

public abstract class Classifier {
    public abstract void classify(PropertiesManager properties,
                                  MersenneTwister mersenneTwister,
                                  ExperimentalData experimentalData,
                                  int k);
}
