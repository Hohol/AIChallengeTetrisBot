package nosubmit.Genetic;

import java.util.*;

public class GeneticAlgorithm<T extends Chromosome<T>> {

    private final T adam;
    private final Random rnd = new Random();
    private static final Comparator<Chromosome> FITNESS_DESC_COMPARATOR = (o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness());

    public GeneticAlgorithm(T adam) {
        this.adam = adam;
    }

    public T search() {
        List<T> population = new ArrayList<>();
        population.add(adam);
        for (int i = 0; i < 50; i++) {
            population.add(adam.mutate(rnd));
        }
        for (int i = 0; i < 10000; i++) {
            population = getNextGeneration(population);
        }
        Collections.sort(population, FITNESS_DESC_COMPARATOR);
        return population.get(0);
    }

    private List<T> getNextGeneration(List<T> population) {
        List<T> nextGeneration = reproduce(population);
        List<T> selected = select(nextGeneration, population.size());
        for (T t : selected) {
            System.out.print(t.getFitness() + " ");
        }
        System.out.println();
        return selected;
    }

    private List<T> reproduce(List<T> population) {
        List<T> nextGeneration = new ArrayList<>();
        for (T t : population) {
            nextGeneration.add(t);
            for (int i = 0; i < 10; i++) {
                nextGeneration.add(t.mutate(rnd));
            }
            for (int i = 0; i < 10; i++) {
                T other = population.get(rnd.nextInt(population.size())); // todo may be same
                T child = t.crossover(other);
                nextGeneration.add(child);
            }
        }
        return nextGeneration;
    }

    private List<T> select(List<T> population, int selectedSize) {
        Collections.sort(population, FITNESS_DESC_COMPARATOR);
        List<T> selected = new ArrayList<>();
        selected.add(population.get(0));
        population.remove(0);
        while (selected.size() < selectedSize) {
            int k = rnd.nextInt(population.size() * population.size());
            int d = (int)Math.sqrt(k);
            int ind = population.size() - 1 - d;
            selected.add(population.get(ind));
            population.remove(ind);
        }
        Collections.sort(selected, FITNESS_DESC_COMPARATOR);
        return selected;
    }
}
