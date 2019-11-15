package com.mtulkanov.tiled;

import java.awt.*;
import java.awt.image.BufferedImage;

class ImageModifier {
    private static final int MAX = 255;

    static BufferedImage redden(BufferedImage source, double percent) {
        var img = copy(source);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                var color = new Color(rgb);
                if (color.equals(Color.BLACK)) {
                    continue;
                }
                var red = color.getRed();
                int newRed = (int) ((MAX - red) * percent + red);
                var newColor = new Color(newRed, color.getGreen(), color.getBlue(), color.getAlpha());
                img.setRGB(x, y, newColor.getRGB());
            }
        }
        return img;
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
