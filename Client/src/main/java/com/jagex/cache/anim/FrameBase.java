package com.jagex.cache.anim;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrameBase {

    private int count;
    private int[][] vertexGroups;
    private int[] transformationType;

    public int[] getLabels(int label) {
        return vertexGroups[label];
    }

    public int getTransformationType(int index) {
        return transformationType[index];
    }
}