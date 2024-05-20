package com.jagex.map;

import com.jagex.Client;
import com.jagex.cache.def.Floor;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.chunk.Chunk;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.object.RenderableObject;
import com.jagex.io.Buffer;
import com.jagex.map.object.GroundDecoration;
import com.jagex.map.tile.SceneTile;
import com.jagex.util.ColourUtils;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.options.Options;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public final class MapRegion {

    private static final int[] anIntArray140 = {16, 32, 64, 128};
    private static final int[] anIntArray152 = {1, 2, 4, 8};
    private static final int[] COSINE_VERTICES = {1, 0, -1, 0};
    private static final int[] SINE_VERTICIES = {0, -1, 0, 1};
    public static boolean lowMemory = false;
    public static int maximumPlane = 99;
    private final int[] anIntArray128;
    private final int[][] tileLighting;
    private final int[] chromas;
    private final int[] hues;
    private final int length;
    private final int[] luminances;
    private final int[] saturations;
    private final int width;
    private final SceneGraph scene;
    public byte[][][] overlayOrientations;
    public short[][][] overlays;
    public byte[][][] manualTileHeight;
    public byte[][][] overlayShapes;
    public byte[][][] shading;
    public byte[][][] tileFlags;
    public int[][][] tileHeights;
    public short[][][] underlays;
    private long lastUpdate = 0;

    public MapRegion(SceneGraph scene, int width, int length) {
        this.scene = scene;
        maximumPlane = 99;
        this.width = width;
        this.length = length;
        tileHeights = new int[4][width + 1][length + 1];
        tileFlags = new byte[4][width][length];
        underlays = new short[4][width][length];
        overlays = new short[4][width][length];
        manualTileHeight = new byte[4][width][length];
        overlayShapes = new byte[4][width][length];
        overlayOrientations = new byte[4][width][length];
        shading = new byte[4][width + 1][length + 1];
        tileLighting = new int[width + 1][length + 1];
        hues = new int[length];
        saturations = new int[length];
        luminances = new int[length];
        chromas = new int[length];
        anIntArray128 = new int[length];

    }

    private static int calculateHeight(int x, int y) {
        int height = interpolatedNoise(x + 45365, y + 0x16713, 4) - 128
                + (interpolatedNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolatedNoise(x, y, 1) - 128 >> 2);
        height = (int) (height * 0.3D) + 35;

        if (height < 10) {
            height = 10;
        } else if (height > 60) {
            height = 60;
        }

        return height;
    }

    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Constants.COSINE[angle * 1024 / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }

    private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
        int adj_x = x / frequencyReciprocal;
        int i1 = x & frequencyReciprocal - 1;
        int adj_y = y / frequencyReciprocal;
        int k1 = y & frequencyReciprocal - 1;
        int l1 = smoothNoise(adj_x, adj_y);
        int i2 = smoothNoise(adj_x + 1, adj_y);
        int j2 = smoothNoise(adj_x, adj_y + 1);
        int k2 = smoothNoise(adj_x + 1, adj_y + 1);
        int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
        int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
        return interpolate(l2, i3, k1, frequencyReciprocal);
    }

    public static int light(int colour, int light) {
        if (colour == -1)
            return 0xbc614e;

        light = light * (colour & 0x7f) / 128;
        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }

        return (colour & 0xff80) + light;
    }

    public static boolean objectReady(int objectId, int type) {
        ObjectDefinition definition = ObjectDefinitionLoader.lookup(objectId);
        if (type == 11) {
            type = 10;
        } else if (type >= 5 && type <= 8) {
            type = 4;
        }

        return definition.ready(type);
    }

    public static boolean objectsReady(byte[] data) {
        boolean ready = true;
        Buffer buffer = new Buffer(data);
        int id = -1;

        while (true) {
            int offset = buffer.readUSmartInt();
            if (offset == 0) {
                return ready;
            }

            id += offset;
            while (true) {
                int terminate;
                terminate = buffer.readUSmart();
                if (terminate == 0) {
                    break;
                }

                int type = buffer.readUByte() >> 2;

                ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
                if (definition == null) {
                    continue;
                }
                if (type != 22 || !lowMemory || definition.isInteractive() || definition.obstructsGround()) {
                    ready &= definition.ready();
                }
            }
        }
    }

    private static int perlinNoise(int x, int y) {
        int n = x + y * 57;
        n = n << 13 ^ n;
        n = n * (n * n * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return n >> 19 & 0xff;
    }

    private static int smoothNoise(int x, int y) {
        int corners = perlinNoise(x - 1, y - 1) + perlinNoise(x + 1, y - 1) + perlinNoise(x - 1, y + 1)
                + perlinNoise(x + 1, y + 1);
        int sides = perlinNoise(x - 1, y) + perlinNoise(x + 1, y) + perlinNoise(x, y - 1) + perlinNoise(x, y + 1);
        int center = perlinNoise(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }

    public void setHeights() {
        for (int z = 0; z < 4; z++) {
            if (length + 1 >= 0) {
                System.arraycopy(tileHeights[z][width - 1], 0, tileHeights[z][width], 0, length + 1);
            }

            for (int x = 0; x <= width; x++) {
                tileHeights[z][x][length] = tileHeights[z][x][length - 1];
            }
        }
    }

    public void unpackObjects(SceneGraph scene, byte[] data, int localX, int localY) {
        decoding:
        {
            Buffer buffer = new Buffer(data);
            int id = -1;

            do {
                int idOffset = buffer.readUSmartInt();
                if (idOffset == 0) {
                    break decoding;
                }

                id += idOffset;
                int position = 0;

                do {
                    int offset = buffer.readUSmartInt();
                    if (offset == 0) {
                        break;
                    }

                    position += offset - 1;
                    int yOffset = position & 0x3f;
                    int xOffset = position >> 6 & 0x3f;
                    int z = position >> 12;

                    if (z >= 4) {
                        z = 3;
                    }
                    int config = buffer.readUByte();
                    int type = config >> 2;
                    int orientation = config & 3;
                    int x = xOffset + localX;
                    int y = yOffset + localY;
                    spawnObjectToWorld(scene, id, x, y, z, type, orientation, false);
                } while (true);
            } while (true);
        }
    }

    public void decodeMapData(Buffer buffer, int x, int y, int z, int regionX, int regionY, int orientation) {// XXX
        if (x >= 0 && x < width && y >= 0 && y < length) {
            tileFlags[z][x][y] = 0;
            do {
                int type = buffer.readUShort();

                if (type == 0) {
                    manualTileHeight[z][x][y] = 0;
                    if (z == 0) {
                        tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
                    } else {
                        tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
                    }

                    return;
                } else if (type == 1) {
                    manualTileHeight[z][x][y] = 1;
                    int height = buffer.readUByte();
                    if (height == 1) {
                        height = 0;
                    }
                    if (z == 0) {
                        tileHeights[0][x][y] = -height * 8;
                    } else {
                        tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
                    }

                    return;
                } else if (type <= 49) {
                    overlays[z][x][y] = (short) buffer.readShort();
                    overlayShapes[z][x][y] = (byte) ((type - 2) / 4);
                    overlayOrientations[z][x][y] = (byte) (type - 2 + orientation & 3);
                } else if (type <= 81) {
                    tileFlags[z][x][y] = (byte) (type - 49);
                } else {
                    underlays[z][x][y] = (short) (type - 81);
                }
            } while (true);
        }

        do {
            int in = buffer.readUShort();
            if (in == 0) {
                break;
            } else if (in == 1) {
                buffer.readUByte();
                return;
            } else if (in <= 49) {
                buffer.readUShort();
            }
        } while (true);
    }

    public void unpackTiles(byte[] data, int dX, int dY, int regionX, int regionY) {
        Buffer buffer = new Buffer(data);
        for (int z = 0; z < 4; z++) {
            for (int localX = 0; localX < 64; localX++) {
                for (int localY = 0; localY < 64; localY++) {
                    decodeMapData(buffer, localX + dX, localY + dY, z, regionX, regionY, 0);
                }
            }
        }
        this.setHeights();
    }

    public void method171(SceneGraph scene) {
        for (int z = 0; z < 4; z++) {
            byte[][] shadowMap = this.shading[z];
            int lightX = -50;
            int lightY = -10;
            int lightZ = -50;
            int lightLength = (int) Math.sqrt((lightX * lightX + lightY * lightY + lightZ * lightZ));
            int distribution = 768 * lightLength >> 8;

            for (int tileY = 1; tileY < length; tileY++) {
                for (int tileX = 1; tileX < width; tileX++) {
                    int x = tileHeights[z][tileX + 1][tileY] - tileHeights[z][tileX - 1][tileY];
                    int y = tileHeights[z][tileX][tileY + 1] - tileHeights[z][tileX][tileY - 1];
                    int length = (int) Math.sqrt((x * x) + (256 * 256) + (y * y));

                    int normalX = (x << 8) / length;
                    int normalY = (256 << 8) / length;
                    int normalZ = (y << 8) / length;

                    int intensity = 96 + ((lightX * normalX) + (lightY * normalY) + (lightZ * normalZ)) / distribution;
                    int subtraction =
                            (shadowMap[tileX - 1][tileY] >> 2) + (shadowMap[tileX + 1][tileY] >> 3) + (shadowMap[tileX][tileY - 1] >> 2) + (
                                    shadowMap[tileX][tileY + 1] >> 3) + (shadowMap[tileX][tileY] >> 1);
                    tileLighting[tileX][tileY] = intensity - subtraction;
                }
            }

            for (int i = 0; i < length; i++) {
                hues[i] = 0;
                saturations[i] = 0;
                luminances[i] = 0;
                chromas[i] = 0;
                anIntArray128[i] = 0;
            }

            for (int x = -5; x < width + 5; x++) {
                for (int y = 0; y < length; y++) {
                    int maxX = x + 5;
                    if (maxX >= 0 && maxX < width) {
                        int id = underlays[z][maxX][y] & 0xFFFF;
                        if (id > 0) {
                            Floor underlay = FloorDefinitionLoader.getUnderlay(id - 1);
                            if (underlay == null) {
                                underlay = FloorDefinitionLoader.getUnderlay(0);
                            }
                            hues[y] += underlay.getWeightedHue();
                            saturations[y] += underlay.getSaturation();
                            luminances[y] += underlay.getLuminance();
                            chromas[y] += underlay.getChroma();
                            anIntArray128[y]++;
                        }
                    }

                    int minX = x - 5;
                    if (minX >= 0 && minX < width) {
                        int id = underlays[z][minX][y] & 0xFFFF;

                        if (id > 0) {
                            Floor underlay = FloorDefinitionLoader.getUnderlay(id - 1);
                            if (underlay == null) {
                                underlay = FloorDefinitionLoader.getUnderlay(0);
                            }
                            hues[y] -= underlay.getWeightedHue();
                            saturations[y] -= underlay.getSaturation();
                            luminances[y] -= underlay.getLuminance();
                            chromas[y] -= underlay.getChroma();
                            anIntArray128[y]--;
                        }
                    }
                }

                if (x >= 0 && x < width) {
                    int hue = 0;
                    int saturation = 0;
                    int lightness = 0;
                    int hueDivisor = 0;
                    int directionTracker = 0;

                    for (int y = -5; y < length + 5; y++) {
                        int maxY = y + 5;
                        if (maxY >= 0 && maxY < length) {
                            hue += hues[maxY];
                            saturation += saturations[maxY];
                            lightness += luminances[maxY];
                            hueDivisor += chromas[maxY];
                            directionTracker += anIntArray128[maxY];
                        }

                        int minY = y - 5;
                        if (minY >= 0 && minY < length) {
                            hue -= hues[minY];
                            saturation -= saturations[minY];
                            lightness -= luminances[minY];
                            hueDivisor -= chromas[minY];
                            directionTracker -= anIntArray128[minY];
                        }

                        if (y >= 0 && y < length) {
                            if (z < maximumPlane) {
                                maximumPlane = z;
                            }

                            int underlayFloorIndex = underlays[z][x][y] & 0xFFFF;
                            int overlayFloorIndex = overlays[z][x][y] & 0xFFFF;

                            if (underlayFloorIndex > 0 || overlayFloorIndex > 0) {
                                int southWestY = tileHeights[z][x][y];
                                int southEastY = tileHeights[z][x + 1][y];
                                int northEastY = tileHeights[z][x + 1][y + 1];
                                int northWestY = tileHeights[z][x][y + 1];
                                int southWestLightness = tileLighting[x][y];
                                int southEastLightness = tileLighting[x + 1][y];
                                int northEastLightness = tileLighting[x + 1][y + 1];
                                int northWestLightness = tileLighting[x][y + 1];

                                int underlayColor = -1;
                                if (underlayFloorIndex > 0) {
                                    if (hueDivisor != 0 && directionTracker != 0) {
                                        underlayColor = Floor.hsl24To16((hue * 256) / hueDivisor, saturation / directionTracker,
                                                lightness / directionTracker);
                                    }
                                }

                                int minimapColor = 0;
                                if (underlayColor != -1) {
                                    minimapColor = GameRasterizer.getInstance().colourPalette[light(underlayColor, 96)];
                                }

                                if (overlayFloorIndex == 0) {
                                    byte flag = tileFlags[z][x][y];
                                    scene.addTile(z, x, y, 0, 0, -1, southWestY, southEastY,
                                            northEastY, northWestY, light(underlayColor, southWestLightness),
                                            light(underlayColor, southEastLightness),
                                            light(underlayColor, northEastLightness),
                                            light(underlayColor, northWestLightness), 0, 0, 0, 0,
                                            minimapColor, minimapColor, -1, 0, 0, true, flag, underlayFloorIndex - 1, overlayFloorIndex - 1);
                                } else {
                                    int tileType = overlayShapes[z][x][y] + 1;
                                    byte orientation = overlayOrientations[z][x][y];
                                    if (overlayFloorIndex - 1 >= FloorDefinitionLoader.getOverlayCount()) {
                                        overlayFloorIndex = FloorDefinitionLoader.getOverlayCount();
                                    }

                                    Floor overlay = FloorDefinitionLoader.getOverlay(overlayFloorIndex - 1);
                                    if (overlay == null) {
                                        continue;
                                    }

                                    int texture = overlay.getTexture();
                                    int hsl;
                                    int rgb;

                                    if (texture > TextureLoader.instance.count()) {
                                        texture = -1;
                                    }

                                    if (texture >= 0 && TextureLoader.getTexture(texture) == null) {
                                        texture = -1;
                                    }

                                    if (texture >= 0) {
                                        rgb = TextureLoader.getTexture(texture).averageTextureColour();
                                        hsl = -1;
                                    } else if (overlay.getRgb() == 0xff00ff) {
                                        rgb = 0;
                                        hsl = -2;
                                        texture = -1;
                                    } else if (overlay.getRgb() == 0x333333) {
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(overlay.getColour(), 96)];
                                        hsl = -2;
                                        texture = -1;
                                    } else {
                                        hsl = ColourUtils.toHsl(overlay.getHue(), overlay.getSaturation(), overlay.getLuminance());
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(overlay.getColour(), 96)];
                                    }

                                    if (rgb == 0x000000 && overlay.getAnotherRgb() != -1) {
                                        int newOverlayColour = ColourUtils.toHsl(overlay.getAnotherHue(), overlay.getAnotherSaturation(), overlay.getAnotherLuminance());
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(newOverlayColour, 96)];

                                    }

                                    byte flag = tileFlags[z][x][y];
                                    scene.addTile(z, x, y, tileType, orientation, texture,
                                            southWestY, southEastY, northEastY, northWestY,
                                            light(underlayColor, southWestLightness),
                                            light(underlayColor, southEastLightness),
                                            light(underlayColor, northEastLightness),
                                            light(underlayColor, northWestLightness),
                                            ColourUtils.checkedLight(hsl, southWestLightness),
                                            ColourUtils.checkedLight(hsl, southEastLightness),
                                            ColourUtils.checkedLight(hsl, northEastLightness),
                                            ColourUtils.checkedLight(hsl, northWestLightness),
                                            minimapColor, rgb, -1, 0,
                                            0, true, flag, underlayFloorIndex - 1, overlayFloorIndex - 1);
                                }
                            }
                        }
                    }
                }
            }

            for (int y = 0; y < length; y++) {
                for (int x = 0; x < width; x++) {
                    scene.setCollisionPlane(x, y, z);
                }
            }
        }

        scene.applyLighting(-50, -10, -50);
        SceneGraph.minimapUpdate = true;
    }

    public byte[] save_terrain_block(Chunk chunk) {
        Buffer buffer = new Buffer(new byte[131072]);
        for (int level = 0; level < 4; level++) {
            for (int x = chunk.offsetX; x < chunk.offsetX + 64; x++) {
                for (int y = chunk.offsetY; y < chunk.offsetY + 64; y++) {
                    save_terrain_tile(level, x, y, buffer);
                }
            }
        }
        return Arrays.copyOf(buffer.getPayload(), buffer.getPosition());
    }

    private void save_terrain_tile(int level, int x, int y, Buffer buffer) {
        if (overlays[level][x][y] != 0) {
            buffer.writeShort(overlayShapes[level][x][y] * 4 + (overlayOrientations[level][x][y] & 3) + 2);
            buffer.writeShort(overlays[level][x][y] & 0xFFFF);
        }
        if (tileFlags[level][x][y] != 0) {
            buffer.writeShort(tileFlags[level][x][y] + 49);
        }
        if (underlays[level][x][y] != 0) {
            buffer.writeShort((underlays[level][x][y] & 0xFFFF) + 81);
        }
        if (manualTileHeight[level][x][y] == 1 || level == 0) {
            buffer.writeShort(1);
            if (level == 0) {
                buffer.writeByte(-tileHeights[level][x][y] / 8);
            } else {
                buffer.writeByte(-(tileHeights[level][x][y] - tileHeights[level - 1][x][y]) / 8);
            }
        } else {
            buffer.writeShort(0);
        }
    }

    public ObjectKey spawnObjectToWorld(SceneGraph scene, int id, int x, int y, int z, int type, int orientation, boolean temporary) {
        maximumPlane = Math.min(z, maximumPlane);

        int centre = tileHeights[z][x][y];
        int east = tileHeights[z][x + 1][y];
        int northEast = tileHeights[z][x + 1][y + 1];
        int north = tileHeights[z][x][y + 1];
        int mean = centre + east + northEast + north >> 2;
        ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

        ObjectKey objectKey = new ObjectKey(x, y, id, type, orientation, definition.isSolid(), definition.isInteractive());


        if (type == 22) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(22, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, 22, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            GroundDecoration deco = scene.addFloorDecoration(x, y, z, object, objectKey, mean, temporary);

            if (deco != null && definition.getMinimapFunction() >= 0 && definition.getMinimapFunction() < Client.mapFunctions.length && definition.getModelIds() != null && definition.getModelIds()[0] == 111) {
                deco.setMinimapFunction(Client.mapFunctions[definition.getMinimapFunction()]);
            } else if (deco != null && definition.getAreaId() >= 0 && definition.getModelIds() != null && definition.getModelIds()[0] == 111) {
                RSArea area = RSAreaLoader.get(definition.getAreaId());
                int func = area.getSpriteId();
                deco.setMinimapFunction(Client.getSingleton().getCache().getSprite(func));
            }

        } else if (type == 10 || type == 11) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, 10, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            if (object != null) {
                int yaw = 0;
                if (type == 11) {
                    yaw += 256;
                }

                int width;
                int length;

                if (orientation == 1 || orientation == 3) {
                    width = definition.getLength();
                    length = definition.getWidth();
                } else {
                    width = definition.getWidth();
                    length = definition.getLength();
                }

                if (scene.addObject(x, y, z, width, length, object, objectKey, yaw, mean, temporary)
                        && definition.isCastsShadow() && !temporary) {
                    Mesh model;
                    if (object instanceof Mesh) {
                        model = (Mesh) object;
                    } else {
                        model = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
                    }
                }
            } else {
                System.out.println("TYPE 10 MODEL NULL");
            }

        } else if (type >= 12) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, type, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            scene.addObject(x, y, z, 1, 1, object, objectKey, 0, mean, temporary);
        } else if (type == 0) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(0, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, 0, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }
            scene.addWall(objectKey, x, y, z, anIntArray152[orientation], object, null, mean, 0, temporary);
            if (!temporary)
                if (orientation == 0) {
                    if (definition.isCastsShadow()) {
                        shading[z][x][y] = 50;
                        shading[z][x][y + 1] = 50;
                    }
                } else if (orientation == 1) {
                    if (definition.isCastsShadow()) {
                        shading[z][x][y + 1] = 50;
                        shading[z][x + 1][y + 1] = 50;
                    }
                } else if (orientation == 2) {
                    if (definition.isCastsShadow()) {
                        shading[z][x + 1][y] = 50;
                        shading[z][x + 1][y + 1] = 50;
                    }
                } else if (orientation == 3) {
                    if (definition.isCastsShadow()) {
                        shading[z][x][y] = 50;
                        shading[z][x + 1][y] = 50;
                    }
                }

            if (!temporary && definition.getDecorDisplacement() != 16) {
                scene.displaceWallDecor(x, y, z, definition.getDecorDisplacement());
            }
        } else if (type == 1) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(1, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, 1, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            scene.addWall(objectKey, x, y, z, anIntArray140[orientation], object, null, mean, 0, temporary);
            if (definition.isCastsShadow() && !temporary) {
                if (orientation == 0) {
                    shading[z][x][y + 1] = 50;
                } else if (orientation == 1) {
                    shading[z][x + 1][y + 1] = 50;
                } else if (orientation == 2) {
                    shading[z][x + 1][y] = 50;
                } else if (orientation == 3) {
                    shading[z][x][y] = 50;
                }
            }

        } else if (type == 2) {
            int oppositeOrientation = orientation + 1 & 3;
            Renderable obj11;
            Renderable obj12;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                obj11 = definition.modelAt(2, 4 + orientation, centre, east, northEast, north, -1);
                obj12 = definition.modelAt(2, oppositeOrientation, centre, east, northEast, north, -1);
            } else {
                obj11 = new RenderableObject(id, 4 + orientation, 2, centre, east, northEast, north,
                        definition.getAnimation(), true);
                obj12 = new RenderableObject(id, oppositeOrientation, 2, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }
            scene.addWall(objectKey, x, y, z, anIntArray152[orientation], obj11, obj12, mean,
                    anIntArray152[oppositeOrientation], temporary);
            if (!temporary && definition.getDecorDisplacement() != 16) {// TODO
                scene.displaceWallDecor(x, y, z, definition.getDecorDisplacement());
            }
        } else if (type == 3) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(3, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, 3, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            scene.addWall(objectKey, x, y, z, anIntArray140[orientation], object, null, mean, 0, temporary);
            if (!temporary && definition.isCastsShadow()) {
                if (orientation == 0) {
                    shading[z][x][y + 1] = 50;
                } else if (orientation == 1) {
                    shading[z][x + 1][y + 1] = 50;
                } else if (orientation == 2) {
                    shading[z][x + 1][y] = 50;
                } else if (orientation == 3) {
                    shading[z][x][y] = 50;
                }
            }

        } else if (type == 9) {
            Renderable object;
            if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
            } else {
                object = new RenderableObject(id, orientation, type, centre, east, northEast, north,
                        definition.getAnimation(), true);
            }

            scene.addObject(x, y, z, 1, 1, object, objectKey, 0, mean, temporary);

        } else {
            if (definition.isContouredGround()) {
                if (orientation == 1) {
                    int tmp = north;
                    north = northEast;
                    northEast = east;
                    east = centre;
                    centre = tmp;
                } else if (orientation == 2) {
                    int tmp = north;
                    north = east;
                    east = tmp;
                    tmp = northEast;
                    northEast = centre;
                    centre = tmp;
                } else if (orientation == 3) {
                    int tmp = north;
                    north = centre;
                    centre = east;
                    east = northEast;
                    northEast = tmp;
                }
            }

            if (type == 4) {
                Renderable object;
                if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                    object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
                } else {
                    object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
                            true);
                }
                scene.addWallDecoration(objectKey, y, orientation * 512, z, 0, mean, object, x, 0, anIntArray152[orientation],
                        temporary);
            } else if (type == 5) {
                int displacement = 16;
                ObjectKey existing = scene.getWallKey(x, y, z);
                if (existing != null) {
                    int existingId = objectKey.getId();
                    displacement = ObjectDefinitionLoader.lookup(existingId).getDecorDisplacement();
                }

                Renderable object;
                if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                    object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
                } else {
                    object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
                            true);
                }

                scene.addWallDecoration(objectKey, y, orientation * 512, z, COSINE_VERTICES[orientation] * displacement, mean,
                        object, x, SINE_VERTICIES[orientation] * displacement, anIntArray152[orientation], temporary);
            } else if (type == 6) {
                Renderable object;
                if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                    object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
                } else {
                    object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
                            true);
                }

                scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 256, temporary);
            } else if (type == 7) {
                Renderable object;
                if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                    object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
                } else {
                    object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
                            true);
                }
                scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 512, temporary);
            } else if (type == 8) {
                Renderable object;
                if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
                    object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
                } else {
                    object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
                            true);
                }
                scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 768, temporary);
            }
        }
        return objectKey;
    }

    public void updateTiles() {
        if (System.currentTimeMillis() - lastUpdate < 200) {
            return;
        }
        lastUpdate = System.currentTimeMillis();

        boolean showBlending = !Options.disableBlending.get();
        boolean hideOverlays = !Options.showOverlay.get();

        for (int z = 0; z < 4; z++) {
            byte[][] shadowMap = this.shading[z];
            int lightX = -50;
            int lightY = -10;
            int lightZ = -50;
            int lightLength = (int) Math.sqrt((lightX * lightX + lightY * lightY + lightZ * lightZ));
            int distribution = 768 * lightLength >> 8;

            for (int tileY = 1; tileY < length; tileY++) {
                for (int tileX = 1; tileX < width; tileX++) {
                    int x = tileHeights[z][tileX + 1][tileY] - tileHeights[z][tileX - 1][tileY];
                    int y = tileHeights[z][tileX][tileY + 1] - tileHeights[z][tileX][tileY - 1];
                    int length = (int) Math.sqrt((x * x) + (256 * 256) + (y * y));

                    int normalX = (x << 8) / length;
                    int normalY = (256 << 8) / length;
                    int normalZ = (y << 8) / length;

                    int intensity = 96 + ((lightX * normalX) + (lightY * normalY) + (lightZ * normalZ)) / distribution;
                    int subtraction =
                            (shadowMap[tileX - 1][tileY] >> 2) + (shadowMap[tileX + 1][tileY] >> 3) + (shadowMap[tileX][tileY - 1] >> 2) + (
                                    shadowMap[tileX][tileY + 1] >> 3) + (shadowMap[tileX][tileY] >> 1);
                    tileLighting[tileX][tileY] = intensity - subtraction;
                }
            }

            for (int i = 0; i < length; i++) {
                hues[i] = 0;
                saturations[i] = 0;
                luminances[i] = 0;
                chromas[i] = 0;
                anIntArray128[i] = 0;
            }

            for (int x = -5; x < width + 5; x++) {
                for (int y = 0; y < length; y++) {
                    int maxX = x + 5;
                    if (maxX >= 0 && maxX < width) {
                        int id = underlays[z][maxX][y] & 0xFFFF;
                        if (id > 0) {
                            Floor underlay = FloorDefinitionLoader.getUnderlay(id - 1);
                            if (underlay == null) {
                                underlay = FloorDefinitionLoader.getUnderlay(0);
                            }
                            hues[y] += underlay.getWeightedHue();
                            saturations[y] += underlay.getSaturation();
                            luminances[y] += underlay.getLuminance();
                            chromas[y] += underlay.getChroma();
                            anIntArray128[y]++;
                        }
                    }

                    int minX = x - 5;
                    if (minX >= 0 && minX < width) {
                        int id = underlays[z][minX][y] & 0xFFFF;

                        if (id > 0) {
                            Floor underlay = FloorDefinitionLoader.getUnderlay(id - 1);
                            if (underlay == null) {
                                underlay = FloorDefinitionLoader.getUnderlay(0);
                            }
                            hues[y] -= underlay.getWeightedHue();
                            saturations[y] -= underlay.getSaturation();
                            luminances[y] -= underlay.getLuminance();
                            chromas[y] -= underlay.getChroma();
                            anIntArray128[y]--;
                        }
                    }
                }

                if (x >= 0 && x < width) {
                    int hue = 0;
                    int saturation = 0;
                    int lightness = 0;
                    int hueDivisor = 0;
                    int directionTracker = 0;

                    for (int y = -5; y < length + 5; y++) {
                        int maxY = y + 5;
                        if (maxY >= 0 && maxY < length) {
                            hue += hues[maxY];
                            saturation += saturations[maxY];
                            lightness += luminances[maxY];
                            hueDivisor += chromas[maxY];
                            directionTracker += anIntArray128[maxY];
                        }

                        int minY = y - 5;
                        if (minY >= 0 && minY < length) {
                            hue -= hues[minY];
                            saturation -= saturations[minY];
                            lightness -= luminances[minY];
                            hueDivisor -= chromas[minY];
                            directionTracker -= anIntArray128[minY];
                        }

                        if (y >= 0 && y < length) {
                            if (z < maximumPlane) {
                                maximumPlane = z;
                            }

                            int underlayFloorIndex = underlays[z][x][y] & 0xFFFF;
                            int overlayFloorIndex = overlays[z][x][y] & 0xFFFF;

                            if (underlayFloorIndex > 0 || overlayFloorIndex > 0) {
                                int southWestY = tileHeights[z][x][y];
                                int southEastY = tileHeights[z][x + 1][y];
                                int northEastY = tileHeights[z][x + 1][y + 1];
                                int northWestY = tileHeights[z][x][y + 1];
                                int southWestLightness = tileLighting[x][y];
                                int southEastLightness = tileLighting[x + 1][y];
                                int northEastLightness = tileLighting[x + 1][y + 1];
                                int northWestLightness = tileLighting[x][y + 1];

                                int underlayColor = -1;
                                if (underlayFloorIndex > 0) {
                                    if (showBlending) {
                                        if (hueDivisor != 0 && directionTracker != 0) {
                                            underlayColor = Floor.hsl24To16((hue * 256) / hueDivisor, saturation / directionTracker,
                                                    lightness / directionTracker);
                                        }
                                    } else {
                                        Floor underlay = FloorDefinitionLoader.getUnderlay(underlayFloorIndex - 1);
                                        if (underlay == null) {
                                            underlay = FloorDefinitionLoader.getUnderlay(0);
                                        }
                                        int h = underlay.getHue();
                                        int s = underlay.getSaturation();
                                        int l = underlay.getLuminance();
                                        if (l < 0) {
                                            l = 0;
                                        } else if (l > 255) {
                                            l = 255;
                                        }
                                        underlayColor = ColourUtils.toHsl(h, s, l);
                                    }
                                }

                                int minimapColor = 0;
                                if (underlayColor != -1) {
                                    minimapColor = GameRasterizer.getInstance().colourPalette[light(underlayColor, 96)];
                                }

                                if (overlayFloorIndex == 0 || hideOverlays) {
                                    byte flag = tileFlags[z][x][y];
                                    scene.addTile(z, x, y, 0, 0, -1, southWestY, southEastY,
                                            northEastY, northWestY, light(underlayColor, southWestLightness),
                                            light(underlayColor, southEastLightness),
                                            light(underlayColor, northEastLightness),
                                            light(underlayColor, northWestLightness), 0, 0, 0, 0,
                                            minimapColor, minimapColor, -1, 0, 0, true, flag, underlayFloorIndex - 1, overlayFloorIndex - 1);
                                } else {
                                    int tileType = overlayShapes[z][x][y] + 1;
                                    byte orientation = overlayOrientations[z][x][y];
                                    if (overlayFloorIndex - 1 >= FloorDefinitionLoader.getOverlayCount()) {
                                        overlayFloorIndex = FloorDefinitionLoader.getOverlayCount();
                                    }

                                    Floor overlay = FloorDefinitionLoader.getOverlay(overlayFloorIndex - 1);
                                    if (overlay == null) {
                                        continue;
                                    }

                                    int texture = overlay.getTexture();
                                    int hsl;
                                    int rgb;

                                    if (texture > TextureLoader.instance.count()) {
                                        texture = -1;
                                    }

                                    if (texture >= 0 && TextureLoader.getTexture(texture) == null) {
                                        texture = -1;
                                    }

                                    if (texture >= 0) {
                                        rgb = TextureLoader.getTexture(texture).averageTextureColour();
                                        hsl = -1;
                                    } else if (overlay.getRgb() == 0xff00ff) {
                                        rgb = 0;
                                        hsl = -2;
                                        texture = -1;
                                    } else if (overlay.getRgb() == 0x333333) {
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(overlay.getColour(), 96)];
                                        hsl = -2;
                                        texture = -1;
                                    } else {
                                        hsl = ColourUtils.toHsl(overlay.getHue(), overlay.getSaturation(), overlay.getLuminance());
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(overlay.getColour(), 96)];
                                    }

                                    if (rgb == 0x000000 && overlay.getAnotherRgb() != -1) {
                                        int newOverlayColour = ColourUtils.toHsl(overlay.getAnotherHue(), overlay.getAnotherSaturation(), overlay.getAnotherLuminance());
                                        rgb = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(newOverlayColour, 96)];

                                    }

                                    byte flag = tileFlags[z][x][y];
                                    scene.addTile(z, x, y, tileType, orientation, texture,
                                            southWestY, southEastY, northEastY, northWestY,
                                            light(underlayColor, southWestLightness),
                                            light(underlayColor, southEastLightness),
                                            light(underlayColor, northEastLightness),
                                            light(underlayColor, northWestLightness),
                                            ColourUtils.checkedLight(hsl, southWestLightness),
                                            ColourUtils.checkedLight(hsl, southEastLightness),
                                            ColourUtils.checkedLight(hsl, northEastLightness),
                                            ColourUtils.checkedLight(hsl, northWestLightness),
                                            minimapColor, rgb, -1, 0,
                                            0, true, flag, underlayFloorIndex - 1, overlayFloorIndex - 1);
                                }
                            } else {
                                SceneTile tile = scene.getTile(z, x, y);
                                tile.simple = null;
                                tile.shape = null;
                            }
                        }
                    }
                }
            }
        }
        SceneGraph.minimapUpdate = true;
    }
}