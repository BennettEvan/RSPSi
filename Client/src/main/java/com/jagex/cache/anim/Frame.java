package com.jagex.cache.anim;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Frame {

    private FrameBase base;
    private int transformationCount;
    private int[] transformX;
    private int[] transformY;
    private int[] transformZ;
    private int[] transformationIndices;
    private boolean opaque = true;

    public int getTransformationIndex(int index) {
        return transformationIndices[index];
    }

    public int getTransformX(int transformation) {
        return transformX[transformation];
    }

    public int getTransformY(int transformation) {
        return transformY[transformation];
    }

    public int getTransformZ(int transformation) {
        return transformZ[transformation];
    }
}