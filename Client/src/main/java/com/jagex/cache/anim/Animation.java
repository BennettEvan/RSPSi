package com.jagex.cache.anim;

import com.jagex.cache.loader.anim.FrameLoader;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Animation {

    private int animatingPrecedence = -1;
    private int[] durations;
    private int frameCount;
    private int[] interleaveOrder;
    private int loopOffset = -1;
    private int[] primaryFrames;
    private int walkingPrecedence = -1;

    public int duration(int frameId) {
        int duration = durations[frameId];
        if (duration == 0) {
            Frame frame = FrameLoader.lookup(primaryFrames[frameId]);
            if (frame != null) {
				durations[frameId] = duration;
            }
        }
        return duration == 0 ? 1 : durations[frameId];
    }

    public int getPrimaryFrame(int index) {
        return primaryFrames[index];
    }
}