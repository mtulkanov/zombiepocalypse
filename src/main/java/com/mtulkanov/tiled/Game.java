package com.mtulkanov.tiled;

import com.mtulkanov.tiled.camera.Camera;
import com.mtulkanov.tiled.graph.*;
import com.mtulkanov.tiled.observer.SubjectImpl;
import com.mtulkanov.tiled.pathfinder.BreadthFirstSearchPathfinder;
import com.mtulkanov.tiled.pathfinder.Pathfinder;
import com.mtulkanov.tiled.player.GraphicsComponent;
import com.mtulkanov.tiled.player.Player;
import com.mtulkanov.tiled.player.InputComponent;
import com.mtulkanov.tiled.system.BattleSystem;
import com.mtulkanov.tiled.tmx.TileMap;
import com.mtulkanov.tiled.tmx.TileObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game implements Runnable {

    public static final Color DEBUG_COLOR = Color.CYAN;
    public static final Color NODE_COLOR = Color.YELLOW;
    public static final Color PATH_COLOR = Color.BLUE;

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private static final Color DARK_GREY = new Color(40, 40, 40);
    private static final Color LIGHT_GREY = new Color(100, 100, 100);
    private static final Color BROWN = new Color(106, 55, 5);

    private static final Color BGCOLOR = BROWN;

    private static final String TILE_MAP_PATH = "maps/tiled.tmx";

    private static final String PLAYER_NAME = "Player";
    private static final String WALL_NAME = "Wall";
    private static final String ZOMBIE_NAME = "Zombie";

    private static Game game;

    private Display display;
    private String title;
    private Thread thread;
    private boolean running = false;
    private double dt;
    private Graphics g;
    private KeyManager keyManager;

    private Player player;
    private Camera camera;

    private List<Obstacle> obstacles;
    private List<Mob> mobs;
    private List<Bullet> bullets;
    private TileMap tileMap;

    private boolean debug = false;

    private BattleSystem battleSystem;

    public static Game getGame() {
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
        tileMap = new TileMap(TILE_MAP_PATH);
        battleSystem = new BattleSystem();
        initState();
    }

    private void initState() {
        this.camera = new Camera(WIDTH, HEIGHT, null);
        player = initPlayer(tileMap);
        obstacles = initObstacles(tileMap);
        mobs = initMobs(tileMap);
        bullets = new LinkedList<>();
    }

    private List<Obstacle> initObstacles(TileMap tileMap) {
        obstacles = new ArrayList<>();
        List<TileObject> tileObjects = tileMap.getTileObjects().get(WALL_NAME);
        if (tileObjects == null) {
            return obstacles;
        }
        for (TileObject tileObject: tileObjects) {
            Vector2 firstPoint = new Vector2(tileObject.getX(), tileObject.getY());
            int width = tileObject.getWidth();
            int height = tileObject.getHeight();
            Obstacle obstacle = new Obstacle(firstPoint, width, height);
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    private Player initPlayer(TileMap tileMap) {
        TileObject tileObject = tileMap.getTileObjects().get(PLAYER_NAME).get(0);
        int x = tileObject.getX() - Assets.player.getWidth() / 2;
        int y = tileObject.getY() - Assets.player.getHeight() / 2;
        Vector2 firstPoint = new Vector2(x, y);
        return new Player(
                firstPoint,
                Assets.player,
                new InputComponent(),
                new GraphicsComponent(),
                new SubjectImpl()
        );
    }

    private List<Mob> initMobs(TileMap tileMap) {
        List<Mob> mobs = new ArrayList<>();
        List<TileObject> tileObjects = tileMap.getTileObjects().get(ZOMBIE_NAME);
        if (tileObjects == null) {
            return mobs;
        }
        List<Node> nodes = NodeLoader.load(tileMap);
        Graph graph = GraphLoader.load(nodes, obstacles);
        for (TileObject tileObject: tileObjects) {
            int x = tileObject.getX() - Assets.player.getWidth() / 2;
            int y = tileObject.getY() - Assets.player.getHeight() / 2;
            Vector2 firstPoint = new Vector2(x, y);
            Pathfinder pathfinder = new BreadthFirstSearchPathfinder(
                    graph,
                    new BreadthFirstSearchWithPath(),
                    obstacles
            );
            Mob mob = new Mob(firstPoint, Assets.mob, pathfinder);
            mobs.add(mob);
        }
        return mobs;
    }

    private void update() {
        var bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        keyManager.update();
        updateDebug();
        camera.update(player);
        battleSystem.update(player, bullets, keyManager);
        player.update(keyManager, dt, g, camera.getOffset());
        mobs.forEach(mob -> mob.update(player));
        bullets.forEach(Bullet::update);
        bullets.removeIf(Bullet::isDespawned);
        mobs.removeIf(Mob::isDead);
    }

    private void updateDebug() {
        if (keyManager.wasClicked(KeyEvent.VK_G)) {
            debug = !debug;
        }
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
        tileMap.render(g, camera.getOffset());
        mobs.forEach(mob -> mob.render(g, camera.getOffset()));
        bullets.forEach(bullet -> bullet.render(g, camera.getOffset()));
        if (debug) {
            obstacles.forEach(obstacle -> obstacle.render(g, camera.getOffset()));
        }

        bs.show();
        g.dispose();
    }

    @Override
    public void run() {
        init();

        final int FPS = 60;
        final double TIME_PER_TICK = 1_000.0 / FPS;
        double delta = 0;
        long now;
        long lastTime = System.currentTimeMillis();
        dt = 0;

        while (running) {
            now = System.currentTimeMillis();
            dt += (now - lastTime) / 1_000.0;
            delta += (now - lastTime) / TIME_PER_TICK;
            lastTime = now;

            if (delta >= 1) {
                var realFps = delta * FPS;
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

    public void reset() {
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

    List<Obstacle> getObstacles() {
        return obstacles;
    }

    List<Mob> getMobs() {
        return mobs;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public boolean isDebug() {
        return debug;
    }
}
