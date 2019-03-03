package edu.gsgp.experiment.data.classifiers;

import edu.gsgp.experiment.data.Classifier;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

public class Void extends Classifier {
    @Override
    public void classify(MersenneTwister mersenneTwister, ExperimentalData experimentalData, int k) {
        for (Utils.DatasetType dataType : Utils.DatasetType.values()) {
            for (Instance instance : experimentalData.getDataset(dataType)) {
                instance.addToGroup(0);
            }
        }
    }
}
