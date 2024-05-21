package com.jagex.cache.loader;

import com.displee.cache.index.archive.Archive;
import com.jagex.io.Buffer;

public interface IndexedLoaderBase<T> {

    T forId(int id);

    int count();

    void init(Archive archive);

    void init(Buffer data, Buffer indexBuffer);
}
