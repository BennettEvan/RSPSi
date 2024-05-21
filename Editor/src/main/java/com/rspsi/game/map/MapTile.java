package com.rspsi.game.map;

import com.jagex.Client;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.map.MapType;
import com.jagex.map.MapRegion;
import com.jagex.map.SceneGraph;
import com.jagex.net.ResourceResponse;
import com.rspsi.cache.CacheFileType;
import net.coobird.thumbnailator.Thumbnails;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapTile {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    public int landscapeId;
    public int objectsId;
    private final int hash;
    private boolean loaded;
    private SceneGraph sceneGraph;
    private MapRegion mapRegion;
    private byte[] landscapeBytes;
    private final int regionX;
    private final int regionY;
    private final RegionView view;

    private MapTile(RegionView view, int x, int y) {
        this.view = view;
        this.hash = (x << 8) + y;
        this.regionX = x;
        this.regionY = y;
        landscapeId = MapIndexLoader.resolve(x, y, MapType.LANDSCAPE);
        objectsId = MapIndexLoader.resolve(x, y, MapType.OBJECT);
    }

    public static boolean exists(int x, int y) {
        return (MapIndexLoader.resolve(x, y, MapType.LANDSCAPE) != -1);
    }

    public static Optional<MapTile> create(RegionView view, int x, int y) {
        if (exists(x, y)) {
            return Optional.of(new MapTile(view, x, y));
        }
        return Optional.empty();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResourceResponse(ResourceResponse response) {
        if (response.request().getType() == CacheFileType.MAP) {
            int fileId = response.request().getFile();
            if (fileId == landscapeId) {
                landscapeBytes = response.decompress();
                landscapeId = -1;
                executorService.submit(() -> {
                    loadChunk();
                    generateImages();
                });
            }
        }
    }

    public void init() {
        EventBus.getDefault().register(this);
        sceneGraph = new SceneGraph(64, 64, 4);
        mapRegion = new MapRegion(sceneGraph, 64, 64);
    }

    public int[] drawMinimapBasic(int plane) {
        int pixels = 256 * 256;
        int[] raster = new int[pixels];
        for (int y = 0; y < 64; y++) {
            int i1 = (63 - y) * 256 * 4;
            for (int x = 0; x < 64; x++) {
                if ((mapRegion.tileFlags[plane][x][y] & 0x18) == 0) {
                    sceneGraph.drawMinimapTile(raster, x, y, plane, i1, 256);
                }
                if (plane < 3 && (mapRegion.tileFlags[plane + 1][x][y] & 8) != 0) {
                    sceneGraph.drawMinimapTile(raster, x, y, plane + 1, i1, 256);
                }
                i1 += 4;
            }
        }
        return raster;
    }

    public void loadChunk() {
        try {
            if (landscapeBytes == null) {
                return;
            }
            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        mapRegion.tileFlags[z][x][y] = 0;
                    }
                }
            }
            mapRegion.unpackTiles(landscapeBytes, 0, 0, regionX, regionY);
            mapRegion.method171(sceneGraph);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadTile() {
        if (loaded) {
            return;
        }
        Client.getSingleton().getProvider().requestMap(landscapeId, hash);
        loaded = true;
	}

    public void generateImages() {
        view.images = new BufferedImage[4];
        for (int z = 0; z < 4; z++) {
            int[] pixels = drawMinimapBasic(z);
            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

            image.setRGB(0, 0, 256, 256, pixels, 0, 256);
            try {
                view.images[z] = Thumbnails.of(image).size(64, 64).asBufferedImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        view.invalidate();
    }
}
