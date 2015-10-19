package genetic.labyrinth;

import genetic.GeneticAlgorithm;

public class MazeMain {

    public static void main(String[] args) throws InterruptedException {
        MazeChromosome lab = new MazeChromosome(
                "" +
                        "...x...x..\n" +
                        "..x..x.x.x\n" +
                        ".x..x..x..\n" +
                        "...x..x...\n" +
                        "xxx..x..x.\n" +
                        "....x..x..\n" +
                        ".xxx..x..x\n" +
                        ".....x..x.\n" +
                        "xxxxx..x..\n" +
                        ".........x"
        );
        //System.out.println(lab.getFitness());/**/

        MazeChromosome adam = lab;
        //MazeChromosome adam = new MazeChromosome(10, 10);
        GeneticAlgorithm<MazeChromosome> geneticAlgorithm = new GeneticAlgorithm<>(adam);
        MazeChromosome best = geneticAlgorithm.search();
        System.out.println(best.getFitness());
        System.out.println(best);
        Thread.sleep(10);/**/
    }

}