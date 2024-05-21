package com.jagex.entity;

import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.model.Mesh;
import com.jagex.util.ObjectKey;
import lombok.Getter;

public class Renderable {

    public boolean selected;
    @Getter protected int modelHeight = 1000;

    public Mesh model() {
        return null;
    }

    public void render(GameRasterizer rasterizer, int x, int y, int orientation, int ySine, int yCosine, int xSine, int xCosine, int height, ObjectKey key, int plane) {
        Mesh model = model();
        if (model != null) {
            modelHeight = model.modelHeight;
            model.render(rasterizer, x, y, orientation, ySine, yCosine, xSine, xCosine, height, key, plane);
        }
    }

    public Mesh asMesh() {
        if (this instanceof Mesh) {
            return (Mesh) this;
        }
        throw new ClassCastException("This is not an instance of mesh!");
    }

    public Renderable copy() {
        return this;
    }
}