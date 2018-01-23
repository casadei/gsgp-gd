/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.pipeline;

import edu.gsgp.population.forestbuilder.ForestBuilder;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Statistics;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.Population;
import edu.gsgp.population.operator.Breeder;

/**
 *
 * @author luiz
 */
public abstract class Pipeline {
    protected PropertiesManager properties;
    protected Statistics stats;
    protected Breeder[] breederArray;
    protected MersenneTwister rndGenerator;
    protected ForestBuilder forestBuilder;
    
    public void setup(PropertiesManager properties, 
                      Statistics stats, 
                      ExperimentalData expData, 
                      MersenneTwister rndGenerator,
                      ForestBuilder forestBuilder){
        this.properties = properties;
        this.stats = stats;
        this.rndGenerator = rndGenerator;
        this.breederArray = properties.getBreederList();
        this.forestBuilder = forestBuilder;
        this.forestBuilder.initialize(rndGenerator);
    }
    
    public abstract Population evolvePopulation(Population originalPop, ExperimentalData expData, int size);

    public abstract Pipeline softClone();

    public void dispose() {
        this.forestBuilder.dispose();
        this.forestBuilder = null;
    }
}
