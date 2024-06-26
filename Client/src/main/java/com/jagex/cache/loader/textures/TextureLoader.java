package com.jagex.cache.loader.textures;

import com.jagex.cache.loader.DataLoaderBase;
import com.jagex.draw.textures.Texture;

public abstract class TextureLoader implements DataLoaderBase<Texture> {

    public static TextureLoader instance;

    public static Texture getTexture(int id) {
        return instance.forId(id);
    }

    public static int[] getTexturePixels(int id) {
        return instance.getPixels(id);
    }

    public static boolean getTextureTransparent(int textureId) {
        return instance.isTransparent(textureId);
    }

    public abstract int[] getPixels(int id);

    public abstract void setBrightness(double exponent);

    public abstract boolean isTransparent(int id);

    @Override
    public void init(byte[] data) {

    }
}
