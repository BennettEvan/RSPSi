package com.jagex.entity.model;

import lombok.Data;

@Data
public class VertexNormal {

    public int x;
    public int y;
    public int z;
    public int magnitude;

    public VertexNormal() {

    }

    public VertexNormal(VertexNormal other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.magnitude = other.magnitude;
    }
}