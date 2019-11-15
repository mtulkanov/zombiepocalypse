package com.mtulkanov.tiled;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Game implements Runnable {
    static final int TILESIZE = 64;

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private static final Color DARK_GREY = new Color(40, 40, 40);
    private static final Color LIGHT_GREY = new Color(100, 100, 100);
    private static final Color BROWN = new Color(106, 55, 5);

    private static final Color BGCOLOR = BROWN;

    private static Game game;

    private Display display;
    private String title;
    private Thread thread;
    private boolean running = false;
    private double dt;
    private Graphics g;
    private KeyManager keyManager;
    private Map map;

    private Player player;
    private Camera camera;

    private List<Wall> walls;
    private List<Mob> mobs;
    private List<Bullet> bullets;

    static Game getGame() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    private Game() {
        this.title = "Tile Game";
        this.keyManager = new KeyManager();
    }

    private void init() {
        display = new Display(title, WIDTH, HEIGHT);
        display.getFrame().addKeyListener(keyManager);
        Assets.init();
        initState();
    }

    private void initState() {
        this.map = new Map("maps/map4.txt");
        this.camera = new Camera(WIDTH, HEIGHT);
        player = map.getPlayer();
        walls = map.getWalls();
        mobs = map.getMobs();
        bullets = new LinkedList<>();
    }

    private void update() {
        keyManager.update();
        player.update();
        mobs.forEach(Mob::update);
        bullets.forEach(Bullet::update);
        bullets.removeIf(Bullet::isDespawned);
        mobs.removeIf(Mob::isDead);
        camera.update(player);
    }

    private void render() {
        var bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        //draw
        g.setColor(BGCOLOR);
        g.fillRect(0, 0, WIDTH, HEIGHT);
//        drawGrid(); for testing
        walls.forEach(wall -> wall.render(g, camera.getOffset()));
        mobs.forEach(mob -> mob.render(g, camera.getOffset()));
        bullets.forEach(bullet -> bullet.render(g, camera.getOffset()));
        player.render(g, camera.getOffset());

        bs.show();
        g.dispose();
    }

    private void drawGrid() {
        g.setColor(LIGHT_GREY);
        for (int x = 0; x < WIDTH; x += TILESIZE) {
            g.drawLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y < HEIGHT; y += TILESIZE) {
            g.drawLine(0, y, WIDTH, y);
        }
    }

    @Override
    public void run() {
        init();

        int fps = 60;
        double timePerTick = 1_000_000_000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        dt = 0;

        while (running) {
            now = System.nanoTime();
            dt += (now - lastTime) / 1_000_000_000.0;
            delta += (now - lastTime) / timePerTick;
            lastTime = now;

            if (delta >= 1) {
                var realFps = delta * fps;
                display.getFrame().setTitle(String.format("FPS %.2f", realFps));
                update();
                render();
                delta--;
                dt = 0;
            }
        }
        stop();
    }

    synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void reset() {
        initState();
    }

    KeyManager getKeyManager() {
        return keyManager;
    }

    Player getPlayer() {
        return player;
    }

    double getDt() {
        return dt;
    }

    Map getMap() {
        return map;
    }

    List<Wall> getWalls() {
        return walls;
    }

    List<Mob> getMobs() {
        return mobs;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
