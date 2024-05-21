package com.jagex;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.jagex.cache.graphics.Sprite;
import com.jagex.net.ResourceProvider;
import com.rspsi.cache.CacheFileType;
import com.rspsi.core.misc.FixedIntegerKeyMap;
import com.rspsi.core.misc.XTEAManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
public class Cache {

    public ResourceProvider resourceProvider;
    @Setter private BiFunction<CacheFileType, Integer, Optional<byte[]>> fileRetrieverOverride;
    @Setter private BiFunction<Integer, Integer, Optional<byte[]>> mapRetrieverOverride;
    @Getter private final CacheLibrary indexedFileSystem;
    private final Index modelArchive;
    private final Index mapArchive;
    private final Index configArchive;
    private final Index skeletonArchive;
    private final Index skinArchive;
    private final Index spriteIndex;
    private final Index textureIndex;
    private final FixedIntegerKeyMap<Sprite> spriteCache = new FixedIntegerKeyMap<>(100);

    public Cache(Path path) {
        log.info("Loading cache at {}", path);

        indexedFileSystem = new CacheLibrary(path.toFile().toString(), false, null);
        modelArchive = indexedFileSystem.index(7);
        mapArchive = indexedFileSystem.index(5);
        configArchive = indexedFileSystem.index(2);
        skeletonArchive = indexedFileSystem.index(0);
        skinArchive = indexedFileSystem.index(1);
        spriteIndex = indexedFileSystem.index(8);
        textureIndex = indexedFileSystem.index(9);

        resourceProvider = new ResourceProvider(this);
        Thread t = new Thread(resourceProvider);
        t.start();
    }

    public Sprite getSprite(int id) {
        if (spriteCache.contains(id)) {
            return spriteCache.get(id);
        }
        Sprite sprite = Sprite.decode(ByteBuffer.wrap(spriteIndex.archive(id).file(0).getData()));
        spriteCache.put(id, sprite);
        return sprite;
    }

    public final Index getFile(CacheFileType index) {
        try {
            return switch (index) {
                case CONFIG -> configArchive;
                case MODEL -> modelArchive;
                case ANIMATION -> skinArchive;
                case SKELETON -> skeletonArchive;
                case MAP -> mapArchive;
                case SPRITE -> spriteIndex;
                case TEXTURE -> textureIndex;
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public final byte[] readMap(int fileId, int regionId) {
        if (mapRetrieverOverride != null) {
            Optional<byte[]> data = mapRetrieverOverride.apply(fileId, regionId);
            if (data.isPresent()) {
                return data.get();
            }
        }
        if (indexedFileSystem.is317()) {
            return mapArchive.archive(fileId).file(0).getData();
        }
        return mapArchive.archive(fileId, XTEAManager.lookupMap(regionId)).file(0).getData();
    }

    public final byte[] getFile(CacheFileType type, int file) {
        try {
            if (fileRetrieverOverride != null) {
                Optional<byte[]> data = fileRetrieverOverride.apply(type, file);
                if (data.isPresent()) {
                    return data.get();
                }
            }
            switch (type) {
                case CONFIG:
                    return configArchive.archive(file).file(0).getData();
                case MODEL:
                    return modelArchive.archive(file).file(0).getData();
                case ANIMATION:
                    return skinArchive.archive(file).file(0).getData();
                case SKELETON:
                    return skeletonArchive.archive(file).file(0).getData();
                case MAP:
                    return mapArchive.archive(file).file(0).getData();
                case TEXTURE:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public final Archive createArchive(int file) {
        return configArchive.archive(file);
    }

    public void close() throws IOException {
        indexedFileSystem.close();
    }

    public ResourceProvider getProvider() {
        return resourceProvider;
    }
}
