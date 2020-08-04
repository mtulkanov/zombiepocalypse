package com.mtulkanov.tiled;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyManager implements KeyListener {
    private static final int DEBOUNCE_PERIOD = 1_000_000_000 / 5;
    private final Map<Integer, Long> lastClickTimestamps;

    private boolean[] keys;
    public boolean up, down, left, right, space, g;

    public KeyManager() {
        keys = new boolean[256];
        lastClickTimestamps = new HashMap<>();
    }

    public void update() {
        up = keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_D];
        space = keys[KeyEvent.VK_SPACE];
        g = keys[KeyEvent.VK_G];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public boolean wasClicked(int button) {
        return keys[button] && wasClickedDebounced(button);
    }

    private boolean wasClickedDebounced(int button) {
        Long lastClicked = lastClickTimestamps.get(button);
        Long current = System.nanoTime();
        if (lastClicked == null
                || (current - lastClicked) >= DEBOUNCE_PERIOD) {
            lastClickTimestamps.put(button, current);
            return true;
        }
        return false;
    }
}
