package edu.gsgp.experiment.data;

import edu.gsgp.utils.MersenneTwister;

public abstract class Classifier {
    public abstract void classify(MersenneTwister mersenneTwister, ExperimentalData experimentalData, int k);
}
