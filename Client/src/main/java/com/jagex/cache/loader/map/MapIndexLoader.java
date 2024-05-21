package com.jagex.cache.loader.map;

import com.displee.cache.index.archive.Archive;
import com.jagex.io.Buffer;

public abstract class MapIndexLoader {

    public static MapIndexLoader instance;

    public static int lookup(MapType type, int hash) {
        return instance.getFileId(hash, type);
    }

    public static int resolve(int regionX, int regionY, MapType type) {
        int hash = (regionX << 8) + regionY;
        return MapIndexLoader.lookup(type, hash);
    }

    public static void setRegionData(int regionX, int regionY, int landscapeId, int objectsId) {
        instance.set(regionX, regionY, landscapeId, objectsId);
    }

    public static String getName(MapType type, int regionX, int regionY) {
        int hash = (regionX << 8) + regionY;
        return MapIndexLoader.instance.getFileName(hash, type);
    }

    public abstract void init(Archive archive);

    public abstract void init(Buffer buffer);

    public abstract int getFileId(int hash, MapType type);

    public abstract void set(int regionX, int regionY, int landscapeId, int objectsId);

    public abstract byte[] encode();

    public String getFileName(int hash, MapType type) {
        return String.valueOf(lookup(type, hash));
    }
}
