package edu.gsgp.population.selector;

import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.utils.MersenneTwister;

import java.util.*;
public class CrowdedTournamentSelector implements IndividualSelector {
    private int tournamentSize;

    public CrowdedTournamentSelector(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public Individual selectIndividual(Population population, MersenneTwister rnd) throws NullPointerException {
        population.nondominatedSort();

        int popSize = population.size();
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < popSize; i++) indexes.add(i);
        List<Individual> tournament = new ArrayList<>();
        for(int i = 0; i < tournamentSize; i++){
            tournament.add(population.get(indexes.remove(rnd.nextInt(indexes.size()))));
        }

        Collections.sort(tournament, (i1, i2) -> {
            if (i1.dominanceRank <= i2.dominanceRank)
                return -1;
            if (i1.dominanceRank > i2.dominanceRank)
                return 1;

            return Double.compare(i2.distance, i1.distance);
        });

        return tournament.get(0);
    }
}
