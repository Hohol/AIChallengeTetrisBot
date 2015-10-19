package genetic;

import java.util.Random;

public interface Chromosome<T extends Chromosome> {
    T mutate(Random rnd);

    T crossover(T other);

    double getFitness(); // bigger fitness is better
}
