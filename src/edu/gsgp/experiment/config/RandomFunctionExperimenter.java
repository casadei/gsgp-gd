/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.experiment.config;

import edu.gsgp.experiment.data.DataProducer;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.nodes.Node;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;
import static edu.gsgp.utils.Utils.DatasetType.TRAINING;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author casadei
 */
public class RandomFunctionExperimenter {
    
    protected CommandLine parameters;
    protected PropertiesManager propertiesManager;
    protected DataProducer dataProducer;
    
    public RandomFunctionExperimenter(String[] args) throws Exception{
        parameters = new DefaultParser().parse(getOptions(), args);
        propertiesManager = new PropertiesManager(parameters);
    }
    
    public void runExperiment(){        
        if (!propertiesManager.isParameterLoaded()) {
            return;
        }

        MersenneTwister random = new MersenneTwister(getSeed());
        List<ExperimentalData> data = getExperimentalData();

        Node tree = propertiesManager.getRandomTree(random);
        evaluate(data, tree);
    } 

    private List<ExperimentalData> getExperimentalData() {
        List<ExperimentalData> data = new ArrayList<>();

        ExperimentalData currentData;
        System.out.println(Utils.sigmoid(819.335284));
        
        while (true) {
            propertiesManager.updateExperimentalData();
            currentData = propertiesManager.getExperimentalData();

            if (data.size() > 1 && data.get(0) == currentData)
                break;
            
            data.add(currentData);
        }
        
        return data;
    }
    
    private void evaluate(List<ExperimentalData> data, Node tree) {
        String output;
        int nodes = tree.getNumNodes() + 1;
        
        for (int fileIndex = 0; fileIndex < data.size(); fileIndex++) {
            Dataset currentDataset = data.get(fileIndex).getDataset(TRAINING);

            for (int instanceIndex = 0; instanceIndex < currentDataset.size(); instanceIndex++) {
                output = String.format("%1d,%2d,%3d,%4f", 
                        nodes, 
                        fileIndex, 
                        instanceIndex, 
                        tree.eval(currentDataset.get(instanceIndex).input)
                );
                
                System.out.println(output);
            }
        }
    }
    
    private Long getSeed() {
        if (parameters.hasOption("s"))
            return Long.parseLong(parameters.getOptionValue("s"));
        
        return System.currentTimeMillis();
    }
        
    private static Options getOptions() {
        Options options = PropertiesManager.getBaseOptions();
        
        options.addOption(Option.builder("s")
                .required(false)
                .hasArg()
                .desc("Seed")
                .type(Long.class)
                .build());
        
        return options;
    }
}
