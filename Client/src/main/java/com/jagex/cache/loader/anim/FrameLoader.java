package com.jagex.cache.loader.anim;

import com.jagex.cache.anim.Frame;

public abstract class FrameLoader {

    public static FrameLoader instance;

    public static Frame lookup(int id) {
        return instance.forId(id);
    }

    public static boolean isInvalid(int frame) {
        return frame == -1;
    }

    protected abstract Frame forId(int id);

    public abstract void load(int id, byte[] data);
}
