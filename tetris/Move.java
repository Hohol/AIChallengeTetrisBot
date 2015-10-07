package tetris;

import static java.awt.event.KeyEvent.*;

public enum Move {
    LEFT(VK_LEFT), RIGHT(VK_RIGHT), DROP(VK_SPACE), ROTATE_CW(VK_UP), ROTATE_CCW(VK_Z);

    private final int keyCode;

    Move(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}