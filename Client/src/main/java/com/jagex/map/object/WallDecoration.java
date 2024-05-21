package com.jagex.map.object;

import com.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class WallDecoration extends DefaultWorldObject {

    private int attributes;
    private int orientation;

    public WallDecoration(ObjectKey id, int x, int y, int z) {
        super(id, x, y, z);
    }

    @Override
    public WorldObjectType getType() {
        return WorldObjectType.WALL_DECORATION;
    }
}