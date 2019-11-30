/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Statistics;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.populator.Populator;
import edu.gsgp.population.pipeline.Pipeline;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSGP {
    private final PropertiesManager properties;
    private final Statistics statistics;
    private final ExperimentalData expData;
    private final MersenneTwister rndGenerator;

    public GSGP(PropertiesManager properties, ExperimentalData expData, MersenneTwister randomGenerator) throws Exception{
        this.properties = properties;
        this.expData = expData;
        statistics = new Statistics(properties, expData);
        rndGenerator = randomGenerator;
    }

    public void evolve() throws Exception {
        boolean canStop = false;

        Populator populator = properties.getPopulationInitializer();
        Pipeline pipe = properties.getPipeline();

        statistics.startClock();

        Population population = populator.populate(rndGenerator, expData, properties.getPopulationSize());
        pipe.setup(properties, statistics, expData, rndGenerator);

        statistics.addGenerationStatistic(population);

        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++) {
            // Evolve a new Population
            Population children = pipe.evolvePopulation(population, expData, properties.getPopulationSize());
            population.resetFronts();

            Population union = new Population(properties);
            union.addAll(population); //Add Parents
            union.addAll(children); //Add Children
            union.nondominatedSort();

            int currentFront = 0, currentPosition = 0;

            Population newPopulation = new Population(properties);

            while (newPopulation.size() < properties.getPopulationSize()) {
                if (currentPosition >= union.getFronts().get(currentFront).size()) {
                    currentFront++;
                    currentPosition = 0;
                }

                Individual current = union.getFronts().get(currentFront).get(currentPosition++);
                newPopulation.add(current);
            }

            union.resetFronts();

            population = newPopulation;

            statistics.addGenerationStatistic(population);

        }

        statistics.finishEvolution(properties.getNumberOfObjectives(), population);
        population.resetFronts();
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
