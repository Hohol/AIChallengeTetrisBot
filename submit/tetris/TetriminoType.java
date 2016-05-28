package tetris;

public enum TetriminoType {
    T(
            "" +
                    ".x.\n" +
                    "xxx"

    ),
    O(
            "" +
                    "xx\n" +
                    "xx"
    ),
    J(
            "" +
                    "x..\n" +
                    "xxx"
    ),
    S(
            "" +
                    ".xx\n" +
                    "xx."
    ),
    I(
            "" +
                    "xxxx"
    ),
    Z(
            "" +
                    "xx.\n" +
                    ".xx"
    ),
    L(
            "" +
                    "..x\n" +
                    "xxx"
    );

    public static TetriminoType[] ALL = values();

    public final boolean[][] b;

    TetriminoType(String s) {
        String[] a = s.split("\n");
        b = new boolean[a.length][a[0].length()];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] = (a[i].charAt(j) == 'x');
            }
        }
    }
}
