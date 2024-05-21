package com.jagex.map.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SpawnedObject extends LinkableWorldObject {

    private int delay;
    private int group;
    private int id;
    private int longetivity = -1;
    private int orientation;
    private int previousId;
    private int previousOrientation;
    private int previousType;
    private int type;

    public SpawnedObject(int id, int x, int y, int z) {
        super(id, x, y, z);
    }

    @Override
    public int getId() {
        return id;
    }

}