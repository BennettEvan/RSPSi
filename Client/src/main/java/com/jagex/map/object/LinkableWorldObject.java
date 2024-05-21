package com.jagex.map.object;

import com.jagex.link.Linkable;
import lombok.Getter;

@Getter
public class LinkableWorldObject extends Linkable {

    private final int x;
    private final int y;
    private final int z;
    private final int id;

    public LinkableWorldObject(int id, int x, int y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
