package com.mtulkanov.tiled.tmx;

import com.mtulkanov.tiled.ImageLoader;
import com.mtulkanov.tiled.Rect;
import com.mtulkanov.tiled.SpriteSheet;
import com.mtulkanov.tiled.Vector2;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class TileMap {

    private static final String MAPS_FOLDER = "maps";
    private static final String IMAGES_FOLDER = "images";
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80_00_00_00;
    private static final int FLIPPED_VERTICALLY_FLAG = 0x40_00_00_00;
    private static final int FLIPPED_DIAGONALLY_FLAG = 0x20_00_00_00;

    private final int width;
    private final int height;
    private final int tilewidth;
    private final int tileheight;
    private final List<Layer> layers;
    private final List<BufferedImage> sprites;
    private final Map<String, List<TileObject>> tileObjects;

    @SneakyThrows
    public TileMap(String filename) {
        Element map = getRoot(filename);
        width = Integer.parseInt(map.getAttribute("width"));
        height = Integer.parseInt(map.getAttribute("height"));
        tilewidth = Integer.parseInt(map.getAttribute("tilewidth"));
        tileheight = Integer.parseInt(map.getAttribute("tileheight"));
        layers = getLayers(map);
        sprites = getSprites(map);
        tileObjects = getTileObjects(map);
    }

    public void render(Graphics g, Vector2 offset) {
        for (Layer layer: layers) {
            for (Tile tile: layer.getTiles()) {
                var renderPos = tile.getRect().getTopLeft().add(offset);
                var sprite = sprites.get(tile.getGid() - 1);
                if (tile.isFlippedDiagonally() && tile.isFlippedHorizontally() && tile.isFlippedVertically()) {
                    var tx = AffineTransform.getScaleInstance(1, -1);
                    tx.rotate(Math.toRadians(90));
                    tx.translate(-tilewidth, -tileheight);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedDiagonally() && tile.isFlippedHorizontally()) {
                    var tx = AffineTransform.getRotateInstance(Math.toRadians(90));
                    tx.translate(0, -tileheight);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedDiagonally() && tile.isFlippedVertically()) {
                    var tx = AffineTransform.getRotateInstance(Math.toRadians(-90));
                    tx.translate(-tilewidth, 0);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedDiagonally()) {
                    var tx = AffineTransform.getScaleInstance(1, -1);
                    tx.rotate(Math.toRadians(-90));
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedHorizontally() && tile.isFlippedVertically()) {
                    var tx = AffineTransform.getRotateInstance(Math.toRadians(180));
                    tx.translate(-tilewidth, -tileheight);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedHorizontally()) {
                    var tx = AffineTransform.getScaleInstance(-1, 1);
                    tx.translate(-tilewidth, 0);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                } else if (tile.isFlippedVertically()) {
                    var tx = AffineTransform.getScaleInstance(1, -1);
                    tx.translate(0, -tileheight);
                    var op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    sprite = op.filter(sprite, null);
                }
                g.drawImage(sprite, renderPos.getIntX(), renderPos.getIntY(), tilewidth, tileheight, null);
            }
        }
    }

    @SneakyThrows
    private Element getRoot(String filename) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream stream = TileMap.class.getClassLoader().getResourceAsStream(filename);
        Document doc = documentBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        return doc.getDocumentElement();
    }

    private List<Layer> getLayers(Element map) {
        List<Layer> layers = new ArrayList<>();
        NodeList layerNodes = map.getElementsByTagName("layer");
        for (int layerIndex = 0; layerIndex < layerNodes.getLength(); layerIndex++) {
            Element layerElement = (Element) layerNodes.item(layerIndex);
            Layer layer = getLayer(layerElement);
            layers.add(layer);
        }
        return layers;
    }

    private Layer getLayer(Element layerElement) {
        List<Tile> tiles = new ArrayList<>();
        Element data = (Element) layerElement.getElementsByTagName("data").item(0);
        NodeList tileNodes = data.getElementsByTagName("tile");
        int x = 0;
        int y = 0;
        for (int tileIndex = 0; tileIndex < tileNodes.getLength(); tileIndex++) {
            Element tileNode = (Element) tileNodes.item(tileIndex);
            String gidAttribute = tileNode.getAttribute("gid");
            if (!gidAttribute.isEmpty()) {
                int gid = Integer.parseUnsignedInt(gidAttribute);
                boolean flippedHorizontrally = (gid & FLIPPED_HORIZONTALLY_FLAG) != 0;
                boolean flippedVertically = (gid & FLIPPED_VERTICALLY_FLAG) != 0;
                boolean flippedDiagonally = (gid & FLIPPED_DIAGONALLY_FLAG) != 0;
                gid &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
                Vector2 firstPoint = new Vector2(x * tilewidth, y * tileheight);
                Rect rect = new Rect(firstPoint, tilewidth, tileheight);
                Tile tile = new Tile(gid, rect, flippedHorizontrally, flippedVertically, flippedDiagonally);
                tiles.add(tile);
            }
            x++;
            if (x >= width) {
                x = 0;
                y++;
            }
        }
        return new Layer(tiles);
    }

    private List<BufferedImage> getSprites(Element map) {
        List<BufferedImage> sprites = new ArrayList<>();
        Element tilesetNode = (Element) map.getElementsByTagName("tileset").item(0);
        String source = MAPS_FOLDER + "/" + tilesetNode.getAttribute("source");
        Element sourceRoot =  getRoot(source);

        int tilewidth = Integer.parseInt(sourceRoot.getAttribute("tilewidth"));
        int tileheight = Integer.parseInt(sourceRoot.getAttribute("tileheight"));
        int spacing = Integer.parseInt(sourceRoot.getAttribute("spacing"));
        int columns = Integer.parseInt(sourceRoot.getAttribute("columns"));
        int tilecount = Integer.parseInt(sourceRoot.getAttribute("tilecount"));
        int rows = tilecount / columns;

        Element image = (Element) sourceRoot.getElementsByTagName("image").item(0);
        String filename = IMAGES_FOLDER + "/" + image.getAttribute("source");
        SpriteSheet spriteSheet = new SpriteSheet(ImageLoader.load(filename));

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                int spriteX = x * tilewidth + x * spacing;
                int spriteY = y * tileheight + y * spacing;
                BufferedImage sprite = spriteSheet.crop(spriteX, spriteY, tilewidth, tileheight);
                sprites.add(sprite);
            }
        }
        return sprites;
    }

    private Map<String, List<TileObject>> getTileObjects(Element map) {
        Map<String, List<TileObject>> newTileObjects = new HashMap<>();
        NodeList objectgroupNodes = map.getElementsByTagName("objectgroup");
        for (int i = 0; i < objectgroupNodes.getLength(); i++) {
            Element objectgroupElement = (Element) objectgroupNodes.item(i);
            Map<String, List<TileObject>> tileObjectsForObjectgroup = getTileObjectsForObjectgroup(objectgroupElement);
            newTileObjects.putAll(tileObjectsForObjectgroup);
        }
        return newTileObjects;
    }

    private Map<String, List<TileObject>> getTileObjectsForObjectgroup(Element objectgroup) {
        NodeList objectNodes = objectgroup.getElementsByTagName("object");
        Map<String, List<TileObject>> tileObjectsForObjectgroup = new HashMap<>();
        for (int objectIndex = 0; objectIndex < objectNodes.getLength(); objectIndex++) {
            Element objectElement = (Element) objectNodes.item(objectIndex);
            String id = objectElement.getAttribute("id");
            int x = Integer.parseInt(objectElement.getAttribute("x"));
            int y = Integer.parseInt(objectElement.getAttribute("y"));
            TileObject tileObject = new TileObject(id, x, y);
            String name = objectElement.getAttribute("name");
            if (!name.isEmpty()) {
                tileObject.setName(name);
            }
            String type = objectElement.getAttribute("type");
            if (!type.isEmpty()) {
                tileObject.setType(type);
            }
            String widthAttr = objectElement.getAttribute("width");
            if (!widthAttr.isEmpty()) {
                int width = Integer.parseInt(widthAttr);
                tileObject.setWidth(width);
            }
            String heightAttr = objectElement.getAttribute("height");
            if (!heightAttr.isEmpty()) {
                int height = Integer.parseInt(heightAttr);
                tileObject.setHeight(height);
            }
            tileObjectsForObjectgroup.compute(name, (key, objects) -> {
                if (objects == null) {
                    objects = new ArrayList<>();
                }
                objects.add(tileObject);
                return objects;
            });
        }
        return tileObjectsForObjectgroup;
    }

    public int getWidthPixels() {
        return width * tilewidth;
    }

    public int getHeightPixels() {
        return height * tileheight;
    }
}
