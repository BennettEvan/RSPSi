package com.jagex.map.object;

import com.jagex.cache.graphics.Sprite;
import com.jagex.util.ObjectKey;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class GroundDecoration extends DefaultWorldObject {

    private Sprite minimapFunction = null;

    public GroundDecoration(ObjectKey id, int x, int y, int renderHeight) {
        super(id, x, y, renderHeight);
    }

    @Override
    public WorldObjectType getType() {
        return WorldObjectType.GROUND_DECORATION;
    }
}