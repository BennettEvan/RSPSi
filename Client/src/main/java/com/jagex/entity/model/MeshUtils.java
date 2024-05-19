package com.jagex.entity.model;

public class MeshUtils {

    public static MeshRevision getRevision(byte[] data) {
        if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
            return MeshRevision.OSRS_TYPE_3;
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
            return MeshRevision.OSRS_TYPE_2;
        }
        return null;
    }
}
