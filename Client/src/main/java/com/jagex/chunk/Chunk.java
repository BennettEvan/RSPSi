package com.jagex.chunk;

import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.graphics.Sprite;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.draw.MainBufferProvider;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.object.AnimableObject;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.jagex.map.object.SpawnedObject;
import com.jagex.net.ResourceResponse;
import com.jagex.util.ObjectKey;
import com.rspsi.cache.CacheFileType;
import com.rspsi.options.Options;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayDeque;
import java.util.List;

public class Chunk {

    private static final GameRasterizer rasterizer = new GameRasterizer();
    private static final int[] mapObjectX = new int[1000];
    private static final int[] mapObjectY = new int[1000];
    private static final byte[] mapObjectSelected = new byte[1000];
    private static final Sprite[] mapObjectSprites = new Sprite[1000];

    static {
        rasterizer.setBrightness(0.6);
    }

    public int tileMapId = -1;
    public int objectMapId = -1;
    public String objectMapName;
    public String tileMapName;
    public int regionHash;
    public byte[] tileMapData;

    public byte[] objectMapData;
    public int offsetX, offsetY;
    public MainBufferProvider minimapImageBuffer = new MainBufferProvider(rasterizer, 256, 256);
    public int regionX, regionY;
    public MapRegion mapRegion;
    public boolean updated = true;
    public SceneGraph scenegraph;
    protected Sprite largeMinimapSprite = new Sprite(256, 256);
    protected BooleanProperty resourceDelivered = new SimpleBooleanProperty(false);
    @Setter protected boolean loaded;
    private Client client;
    private int mapObjectCount;
    private ArrayDeque<AnimableObject> incompleteAnimables;
    private ArrayDeque<SpawnedObject> spawns;
    private boolean ready = false;
    @Setter private boolean newMap;

    protected Chunk() {
    }

    public Chunk(int hash) {
        this.regionHash = hash;
        this.regionX = (hash >> 8) & 0xff;
        this.regionY = hash & 0xff;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResourceResponse(ResourceResponse response) {
        if (response.request().getType() == CacheFileType.MAP) {
            int fileId = response.request().getFile();
            if (fileId == tileMapId) {
                tileMapData = response.decompress();
                resourceDelivered.set(true);
            } else if (fileId == objectMapId) {
                objectMapData = response.decompress();
                resourceDelivered.set(true);
            }
        }
    }

    private void drawOnMinimap(Sprite sprite, int x, int y, boolean selected) {
        if (sprite == null) {
			return;
		}
        sprite.drawSprite(rasterizer, x * 4 - sprite.getResizeWidth() / 2, 256 - (y * 4 + sprite.getResizeHeight() / 2), selected);
    }

    public void init(Client client) {
        EventBus.getDefault().register(this);
        this.client = client;
        this.scenegraph = client.sceneGraph;
        this.mapRegion = client.mapRegion;

        incompleteAnimables = new ArrayDeque<>();
        spawns = new ArrayDeque<>();
    }

    public void drawMinimap() {
        if (!updated) {
			return;
		}
        minimapImageBuffer.initializeRasterizer();
        largeMinimapSprite.drawSprite(rasterizer, 0, 0);
        for (int i = 0; i < mapObjectCount; i++) {
            int x = mapObjectX[i];
            int y = mapObjectY[i];
            boolean selected = mapObjectSelected[i] == 1;
            drawOnMinimap(mapObjectSprites[i], x, y, selected);
        }
    }

    public void drawMinimapScene(int plane) {
        if (!updated) {
            return;
        }

        int[] raster = largeMinimapSprite.getRaster();
        int pixels = raster.length;
        for (int i = 0; i < pixels; i++) {
            raster[i] = 0;
        }

        for (int y = 0; y < 64; y++) {
            int i1 = (63 - y) * 256 * 4;
            for (int x = 0; x < 64; x++) {
                if ((mapRegion.tileFlags[plane][offsetX + x][offsetY + y] & 0x18) == 0) {
                    scenegraph.drawMinimapTile(raster, offsetX + x, offsetY + y, plane, i1, 256);
                }

                if (plane < 3 && (mapRegion.tileFlags[plane + 1][offsetX + x][offsetY + y] & 8) != 0) {
                    scenegraph.drawMinimapTile(raster, offsetX + x, offsetY + y, plane + 1, i1, 256);
                }
                i1 += 4;
            }
        }

        int j1 = 0xFFFFFF;
        int l1 = 0xFF0000;
        largeMinimapSprite.initRaster(rasterizer);
        if (Options.showObjects.get()) {
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) {
                    if ((mapRegion.tileFlags[plane][offsetX + x][offsetY + y] & 0x18) == 0) {
                        method50(x, y, plane, j1, l1);
                    }
                    if (plane < 3 && (mapRegion.tileFlags[plane + 1][offsetX + x][offsetY + y] & 8) != 0) {
                        method50(x, y, plane + 1, j1, l1);
                    }
                }
            }

            mapObjectCount = 0;
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    ObjectKey key = scenegraph.getFloorDecorationKey(offsetX + x, offsetY + y, plane);

                    if (key != null) {
                        byte selected = (byte) (scenegraph.getTileFloorDecoration(offsetX + x, offsetY + y, plane).isSelected() ? 1 : 0);
                        int id = key.getId();
                        ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
                        if (definition != null) {
                            if (definition.getAreaId() != -1) {
                                RSArea area = RSAreaLoader.get(definition.getAreaId());
                                int function = area.getSpriteId();

                                if (function >= 0) {

                                    mapObjectSprites[mapObjectCount] = client.getCache().getSprite(function);
                                    mapObjectSelected[mapObjectCount] = selected;
                                    mapObjectX[mapObjectCount] = x;
                                    mapObjectY[mapObjectCount] = y;
                                    mapObjectCount++;
                                }
                            } else {
                                int function = definition.getMinimapFunction();
                                if (function >= 0 && function < Client.mapFunctions.length) {
                                    mapObjectSprites[mapObjectCount] = Client.mapFunctions[function];
                                    mapObjectSelected[mapObjectCount] = selected;
                                    mapObjectX[mapObjectCount] = x;
                                    mapObjectY[mapObjectCount] = y;
                                    mapObjectCount++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final void method115() {
        spawns.forEach(spawn -> {
            if (spawn.getLongetivity() > 0) {
                spawn.setLongetivity(spawn.getLongetivity() - 1);
            }

            if (spawn.getLongetivity() == 0) {
                if (spawn.getPreviousId() < 0
                        || MapRegion.objectReady(spawn.getPreviousId(), spawn.getPreviousType())) {
                    removeObject(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getGroup(),
                            spawn.getPreviousOrientation(), spawn.getPreviousType(), spawn.getPreviousId());
                    spawn.unlink();
                }
            } else {
                if (spawn.getDelay() > 0) {
                    spawn.setDelay(spawn.getDelay() - 1);
                }
                if (spawn.getDelay() == 0 && spawn.getX() >= 1 && spawn.getY() >= 1 && spawn.getX() <= 102
                        && spawn.getY() <= 102
                        && (spawn.getId() < 0 || MapRegion.objectReady(spawn.getId(), spawn.getType()))) {
                    removeObject(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getGroup(), spawn.getOrientation(),
                            spawn.getType(), spawn.getId());
                    spawn.setDelay(-1);
                    if (spawn.getId() == spawn.getPreviousId() && spawn.getPreviousId() == -1) {
                        spawn.unlink();
                    } else if (spawn.getId() == spawn.getPreviousId()
                            && spawn.getOrientation() == spawn.getPreviousOrientation()
                            && spawn.getType() == spawn.getPreviousType()) {
                        spawn.unlink();
                    }
                }
            }
        });

        spawns.clear();
    }

    public void loadChunk() {
        scenegraph.setChunk(this);
        incompleteAnimables.clear();

        if (tileMapData != null) {
            mapRegion.unpackTiles(tileMapData, offsetX, offsetY, regionX, regionY);
        }
        if (objectMapData != null) {
            mapRegion.unpackObjects(scenegraph, objectMapData, offsetX, offsetY);
        }
        method63();
        this.loaded = true;
        updated = true;
    }

    public final void method50(int x, int y, int z, int nullColour, int defaultColour) {
        ObjectKey key = scenegraph.getWallKey(offsetX + x, offsetY + y, z);

        if (key != null) {
            int id = key.getId();
            int type = key.getType();
            int orientation = key.getOrientation();

            int colour = nullColour;
            if (key.isInteractive()) {
                colour = defaultColour;
            }

            int[] raster = largeMinimapSprite.getRaster();
            int k4 = x * 4 + (63 - y) * 256 * 4;
            ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

            if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
                Sprite image = Client.mapScenes[definition.getMapscene()];
                if (image != null) {
                    int dx = (definition.getWidth() * 4 - image.getWidth()) / 2;
                    int dy = (definition.getLength() * 4 - image.getHeight()) / 2;
                    image.drawSprite(rasterizer, 48 + x * 4 + dx, 48 + (63 - y - definition.getLength()) * 4 + dy);
                }
            } else {
                if (type == 0 || type == 2) {
                    if (orientation == 0) {
                        raster[k4] = colour;
                        raster[k4 + 256] = colour;
                        raster[k4 + 256 * 2] = colour;
                        raster[k4 + 256 * 3] = colour;
                    } else if (orientation == 1) {
                        raster[k4] = colour;
                        raster[k4 + 1] = colour;
                        raster[k4 + 2] = colour;
                        raster[k4 + 3] = colour;
                    } else if (orientation == 2) {
                        raster[k4 + 3] = colour;
                        raster[k4 + 3 + 256] = colour;
                        raster[k4 + 3 + 256 * 2] = colour;
                        raster[k4 + 3 + 256 * 3] = colour;
                    } else if (orientation == 3) {
                        raster[k4 + 256 * 3] = colour;
                        raster[k4 + 256 * 3 + 1] = colour;
                        raster[k4 + 256 * 3 + 2] = colour;
                        raster[k4 + 256 * 3 + 3] = colour;
                    }
                }
                if (type == 3) {
                    if (orientation == 0) {
                        raster[k4] = colour;
                    } else if (orientation == 1) {
                        raster[k4 + 3] = colour;
                    } else if (orientation == 2) {
                        raster[k4 + 3 + 256 * 3] = colour;
                    } else if (orientation == 3) {
                        raster[k4 + 256 * 3] = colour;
                    }
                }
                if (type == 2) {
                    if (orientation == 3) {
                        raster[k4] = colour;
                        raster[k4 + 256] = colour;
                        raster[k4 + 256 * 2] = colour;
                        raster[k4 + 256 * 3] = colour;
                    } else if (orientation == 0) {
                        raster[k4] = colour;
                        raster[k4 + 1] = colour;
                        raster[k4 + 2] = colour;
                        raster[k4 + 3] = colour;
                    } else if (orientation == 1) {
                        raster[k4 + 3] = colour;
                        raster[k4 + 3 + 256] = colour;
                        raster[k4 + 3 + 256 * 2] = colour;
                        raster[k4 + 3 + 256 * 3] = colour;
                    } else if (orientation == 2) {
                        raster[k4 + 256 * 3] = colour;
                        raster[k4 + 256 * 3 + 1] = colour;
                        raster[k4 + 256 * 3 + 2] = colour;
                        raster[k4 + 256 * 3 + 3] = colour;
                    }
                }
            }
        }

        key = scenegraph.getInteractableObjectKey(offsetX + x, offsetY + y, z);
        if (key != null) {
            int id = key.getId();
            int type = key.getType();
            int orientation = key.getOrientation();
            ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

            if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
                Sprite image = Client.mapScenes[definition.getMapscene()];
                if (image != null) {
                    int j5 = (definition.getWidth() * 4 - image.getWidth()) / 2;
                    int k5 = (definition.getLength() * 4 - image.getHeight()) / 2;
                    image.drawSprite(rasterizer, x * 4 + j5, (63 - y - definition.getLength()) * 4 + k5);
                }
            } else if (type == 9) {
                int colour = 0xeeeeee;
                if (key.isInteractive()) {
                    colour = 0xee0000;
                }

                int[] raster = largeMinimapSprite.getRaster();
                int index = x * 4 + (63 - y) * 256 * 4;
                if (orientation == 0 || orientation == 2) {
                    raster[index + 256 * 3] = colour;
                    raster[index + 256 * 2 + 1] = colour;
                    raster[index + 256 + 2] = colour;
                    raster[index + 3] = colour;
                } else {
                    raster[index] = colour;
                    raster[index + 256 + 1] = colour;
                    raster[index + 256 * 2 + 2] = colour;
                    raster[index + 256 * 3 + 3] = colour;
                }
            }
        }

        key = scenegraph.getFloorDecorationKey(offsetX + x, offsetY + y, z);
        if (key != null) {
            int id = key.getId();
            ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
            if (definition != null && definition.getMapscene() != -1 && definition.getMapscene() < Client.mapScenes.length) {
                Sprite image = Client.mapScenes[definition.getMapscene()];
                if (image != null) {
                    int i4 = (definition.getWidth() * 4 - image.getWidth()) / 2;
                    int j4 = (definition.getLength() * 4 - image.getHeight()) / 2;
                    image.drawSprite(rasterizer, x * 4 + i4, (63 - y - definition.getLength()) * 4 + j4);
                }
            }
        }
    }

    private void method63() {
        spawns.forEach(spawn -> {
            if (spawn.getLongetivity() == -1) {
                spawn.setDelay(0);
                setPreviousObject(spawn);
            } else {
                spawn.unlink();
            }
        });
        spawns.clear();
    }

    public final void processAnimableObjects() {
        List<AnimableObject> completed = Lists.newArrayList();
        incompleteAnimables.stream()
                .filter(object -> object.getZ() != Options.currentHeight.get() || object.isTransformationCompleted())
                .forEach(completed::add);

        incompleteAnimables.removeAll(completed);
        completed.clear();

        incompleteAnimables.forEach(object -> {
            if (Client.pulseTick >= object.getTick()) {
                object.nextAnimationStep(Client.tickDelta);
                if (!object.isTransformationCompleted()) {
                    scenegraph.addEntity(object.getX(), object.getY(), object.getZ(), object, 0, null,
                            object.getRenderHeight(), 60, false, false);
                }
            }
        });
    }

    public boolean ready() {
        if (ready) {
            return true;
        }
        if (newMap) {
            return true;
        }
        if (tileMapId != -1 && tileMapData == null) {
            return false;
        }
        if (objectMapId != -1 && objectMapData == null) {
            return false;
        } else if (objectMapId != -1) {
            if (!MapRegion.objectsReady(objectMapData)) {
                return false;
            }
        }
        loadChunk();
        ready = true;
        return true;
    }

    private void removeObject(int x, int y, int z, int group, int previousOrientation, int previousType, int previousId) {
        if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {
            ObjectKey key = null;
            if (group == 0) {
                key = scenegraph.getWallKey(x, y, z);
            } else if (group == 1) {
                key = scenegraph.getWallDecorationKey(x, y, z);
            } else if (group == 2) {
                key = scenegraph.getInteractableObjectKey(x, y, z);
            } else if (group == 3) {
                key = scenegraph.getFloorDecorationKey(x, y, z);
            }

            if (key != null) {
                int id = key.getId();

                if (group == 0) {
                    scenegraph.removeWall(x, y, z);
                } else if (group == 1) {
                    scenegraph.removeWallDecoration(x, y, z);
                } else if (group == 2) {
                    scenegraph.removeObject(x, y, z);
                    ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
                    if (x + definition.getWidth() > 63 || y + definition.getWidth() > 63
                            || x + definition.getLength() > 63 || y + definition.getLength() > 63) {
                        return;
                    }
                } else {
                    scenegraph.removeFloorDecoration(x, y, z);
                }
            }

            if (previousId >= 0) {
                mapRegion.spawnObjectToWorld(scenegraph, previousId, x, y, z, previousType, previousOrientation, false);
            }
        }
    }

    private void setPreviousObject(SpawnedObject spawn) {
        ObjectKey key = null;
        int id = -1;
        int type = 0;
        int orientation = 0;

        if (spawn.getGroup() == 0) {
            key = scenegraph.getWallKey(spawn.getX(), spawn.getY(), spawn.getZ());
        } else if (spawn.getGroup() == 1) {
            key = scenegraph.getWallDecorationKey(spawn.getX(), spawn.getY(), spawn.getZ());
        } else if (spawn.getGroup() == 2) {
            key = scenegraph.getInteractableObjectKey(spawn.getX(), spawn.getY(), spawn.getZ());
        } else if (spawn.getGroup() == 3) {
            key = scenegraph.getFloorDecorationKey(spawn.getX(), spawn.getY(), spawn.getZ());
        }

        if (key != null) {
            id = key.getId();
            type = key.getType();
            orientation = key.getOrientation();
        }

        spawn.setPreviousId(id);
        spawn.setPreviousType(type);
        spawn.setPreviousOrientation(orientation);
    }

    public boolean hasLoaded() {
        return loaded;
    }

    public boolean inChunk(int wTileX, int wTileY) {
        return wTileX >= offsetX && wTileX < offsetX + 64 && wTileY >= offsetY && wTileY < offsetY + 64;
    }

    public void checkForUpdate() {
        for (int x = offsetX; x < offsetX + 64; x++) {
            for (int y = offsetY; y < offsetY + 64; y++) {
                if (scenegraph.getTile(Options.currentHeight.get(), x, y).hasUpdated) {
                    this.updated = true;
                    break;
                }
            }
        }
    }

    public void clearUpdates() {
        for (int x = offsetX; x < offsetX + 64; x++) {
            for (int y = offsetY; y < offsetY + 64; y++) {
                scenegraph.getTile(Options.currentHeight.get(), x, y).hasUpdated = false;
            }
        }
    }

    public void fillNamesFromIds() {
        if (tileMapName == null || tileMapName.isEmpty()) {
            tileMapName = Integer.toString(tileMapId);
        }
        if (objectMapName == null || objectMapName.isEmpty()) {
            objectMapName = Integer.toString(objectMapId);
        }
    }
}
