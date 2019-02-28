/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.experiment.config.PropertiesManager;

import java.util.*;


/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Population extends ArrayList<Individual> {
    private PropertiesManager properties;
    private List<List<Individual>> fronts = new ArrayList<>();
    private boolean sorted = false;
    
    public Population(PropertiesManager properties) {
        super();

        this.properties = properties;
    }
    
    public Population(PropertiesManager properties, ArrayList<Individual> individuals) {
        super(individuals);

        this.properties = properties;
    }
    
    public void addAll(Individual[] newIndividuals){
        addAll(Arrays.asList(newIndividuals));
    }

    public boolean dominates(Individual a, Individual b) {
        for (int i = 0; i < a.getFitness().length; i++) {
            if (a.getFitness()[i] > b.getFitness()[i]) {
                return false;
            }
        }

        return true;
    }

    public void resetFronts() {
        for (List<Individual> front : fronts) {
            for (Individual individual : front) {
                individual.resetDominance();
            }

            front.clear();
        }

        fronts.clear();
    }

    public void nondominatedSort() {
        if (sorted)
            return;

        resetFronts();

        fronts = new ArrayList<>();

        List<Individual> front = new ArrayList<>();

        for (Individual i1 : this) {
            i1.resetDominance();

            for (Individual i2 : this) {
                if (i1 == i2)
                    continue;

                if (dominates(i1, i2))
                    i1.dominanceSet.add(i2);
                else if (dominates(i2, i1))
                    i1.dominanceCount++;
            }

            if (i1.dominanceCount == 0) {
                i1.dominanceRank = 0;
                front.add(i1);
            }
        }

        fronts.add(front);

        int current = 0;

        do {
            front = new ArrayList<>();

            for (Individual i1 : fronts.get(current)) {
                for (Individual i2 : i1.dominanceSet) {
                    i2.dominanceCount--;

                    if (i2.dominanceCount == 0) {
                        i2.dominanceRank = current + 1;
                        front.add(i2);
                    }
                }
            }

            current++;

            if (!front.isEmpty())
                fronts.add(front);

        } while (current < fronts.size());

        for (List<Individual> currFront : fronts) {
            computeDistances(currFront);
        }

        sorted = true;
    }

    private void computeDistances(List<Individual> front) {
        for (Individual ind : front) {
            ind.distance = 0;
        }

        int objectives = front.get(0).getFitness().length;

        for (int i = 0; i < objectives; i++) {
            Double min = front.get(0).getFitness()[i];
            Double max = front.get(0).getFitness()[i];

            for (Individual ind : front) {
                if (ind.getFitness()[i] < min)
                    min = ind.getFitness()[i];
                if (ind.getFitness()[i] > max)
                    max = ind.getFitness()[i];
            }

            double range = max - min;

            front.get(0).distance = Double.MAX_VALUE;
            front.get(front.size() - 1).distance = Double.MAX_VALUE;

            if (range == 0)
                continue;

            for (int j = 1; j < front.size() - 1; j++) {
                front.get(j).distance += (front.get(j + 1).getFitness()[i] - front.get(j - 1).getFitness()[i]) / range;
            }

        }
    }

    public Individual[] getBestIndividuals(){
        nondominatedSort();

        Collections.sort(fronts.get(0), (i1, i2) -> {
            if (i1.dominanceRank <= i2.dominanceRank)
                return -1;
            if (i1.dominanceRank > i2.dominanceRank)
                return 1;

            return Double.compare(i2.distance, i1.distance);
        });

        Map<Integer, Individual> minimum = new HashMap<>();

        for (Individual individual : fronts.get(0)) {
            for (int i = 0; i < properties.getNumberOfObjectives(); i++) {
                if (!minimum.containsKey(i) || individual.getTrainingFitness()[i] < minimum.get(i).getTrainingFitness()[i])
                    minimum.put(i, individual);
            }
        }

        List<Individual> result = new ArrayList<>();

        for (int i = 0; i < properties.getNumberOfObjectives(); i++) {
            if (!result.contains(minimum.get(i)))
                result.add(minimum.get(i));
        }

        Individual[] output = new Individual[result.size()];

        for (int i = 0; i < result.size(); i++) {
            output[i] = result.get(i);
        }

        return output;
    }
}
