package nosubmit.Genetic.labyrinth;

import nosubmit.Genetic.Chromosome;

import java.util.Random;

public class MazeChromosome implements Chromosome<MazeChromosome> {

    private final static char WALL = 'x';
    private final static char EMPTY = '.';

    private final int n, m;
    private final char[][] a;
    private double fitness = -1;

    public MazeChromosome(int n, int m) {
        this.n = n;
        this.m = m;
        a = new char[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = EMPTY;
            }
        }
    }

    public MazeChromosome(String s) {
        String[] sa = s.split("\n");
        n = sa.length;
        m = sa[0].length();
        a = new char[n][];
        for (int i = 0; i < m; i++) {
            a[i] = sa[i].toCharArray();
        }
    }

    @Override
    public MazeChromosome mutate(Random rnd) {
        MazeChromosome mutant = new MazeChromosome(n, m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                mutant.a[i][j] = a[i][j];
            }
        }
        int x = rnd.nextInt(n);
        int y = rnd.nextInt(m);
        if (mutant.a[x][y] == WALL) {
            mutant.a[x][y] = EMPTY;
        } else {
            mutant.a[x][y] = WALL;
        }
        return mutant;
    }

    @Override
    public MazeChromosome crossover(MazeChromosome other) {
        MazeChromosome child = new MazeChromosome(n, m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i < n / 2) {
                    child.a[i][j] = a[i][j];
                } else {
                    child.a[i][j] = other.a[i][j];
                }
            }
        }
        return child;
    }

    @Override
    public double getFitness() {
        if (fitness < 0) {
            fitness = getFitness0();
        }
        return fitness;
    }

    private double getFitness0() {
        if (!hasEmpty()) {
            return 0;
        }
        if (!connected()) {
            return 0;
        }
        int r = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] != EMPTY) {
                    continue;
                }
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        if (Math.abs(di) + Math.abs(dj) == 1) {
                            int toI = i + di;
                            int toJ = j + dj;
                            if (!inside(toI, toJ) || a[toI][toJ] == WALL) {
                                r++;
                            }
                        }
                    }
                }
            }
        }
        return r;
    }

    private boolean hasEmpty() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean connected() {
        boolean[][] visited = new boolean[n][m];
        fori:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] == EMPTY) {
                    dfs(i, j, visited);
                    break fori;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] == EMPTY && !visited[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void dfs(int x, int y, boolean[][] visited) {
        if (!inside(x, y)) {
            return;
        }
        if (a[x][y] == WALL) {
            return;
        }
        if (visited[x][y]) {
            return;
        }
        visited[x][y] = true;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) + Math.abs(dy) == 1) {
                    dfs(x + dx, y + dy, visited);
                }
            }
        }
    }

    private boolean inside(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < m;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fitness = ").append(fitness).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                sb.append(a[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
