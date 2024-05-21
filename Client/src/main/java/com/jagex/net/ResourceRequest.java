package com.jagex.net;

import com.rspsi.cache.CacheFileType;
import lombok.Getter;

@Getter
public class ResourceRequest {

    private final int file;
    private final CacheFileType type;
    private final long requestTime;

    public ResourceRequest(int file, CacheFileType type) {
        this.file = file;
        this.type = type;
        this.requestTime = System.currentTimeMillis();
    }

    public long getAge() {
        return System.currentTimeMillis() - requestTime;
    }
}