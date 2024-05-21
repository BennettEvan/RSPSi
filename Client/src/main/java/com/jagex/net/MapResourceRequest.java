package com.jagex.net;

import com.rspsi.cache.CacheFileType;
import lombok.Getter;

@Getter
public class MapResourceRequest extends ResourceRequest {

    private final int regionId;

    public MapResourceRequest(int regionId, int file) {
        super(file, CacheFileType.MAP);
        this.regionId = regionId;
    }
}