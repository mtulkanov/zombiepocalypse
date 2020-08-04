package com.mtulkanov.tiled;

import java.awt.image.BufferedImage;

public class Assets {
    public static BufferedImage player, playerDamaged, wall, mob, bullet;

    static void init() {
        var characters = new SpriteSheet(ImageLoader.load("images/characters.png"));
        player = characters.crop(263, 132, 49, 43);
        playerDamaged = ImageModifier.redden(player, 0.5);
        mob = characters.crop(424, 0, 35, 43);

        wall = ImageLoader.load("images/tileGreen_39.png");
        bullet = ImageLoader.load("images/bullet.png");
    }
}
