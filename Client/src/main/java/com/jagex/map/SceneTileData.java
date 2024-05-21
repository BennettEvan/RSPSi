package com.jagex.map;

import com.google.common.collect.Lists;
import com.jagex.map.tile.SceneTile;
import com.rspsi.core.misc.CopyOptions;
import com.rspsi.core.misc.ExportOptions;
import com.rspsi.core.misc.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SceneTileData {

    private short overlayId = -1, underlayId = -1;
    private byte overlayOrientation = -1, overlayType = -1;

    private int tileHeight = -1;

    private int[] gameObjectIds;
    private int[] gameObjectConfigs;

    private int wallId = -1;
    private int wallConfig = -1;

    private int wallDecoId = -1;
    private int wallDecoConfig = -1;

    private int groundDecoId = -1;
    private int groundDecoConfig = -1;

    private byte tileFlag;

    private int x, y, z;

    public SceneTileData() {

    }

    public SceneTileData(ExportOptions exportOptions, SceneTile tile, int tileX, int tileY) {
        if (exportOptions.exportGameObjects()) {
            if (tile.objectCount > 0) {
                List<Integer> ids = Lists.newArrayList();
                List<Integer> configs = Lists.newArrayList();
                for (int i = 0; i < tile.objectCount; i++) {
                    if (tile.gameObjects[i].getX() == tileX && tile.gameObjects[i].getY() == tileY) {
                        ids.add(tile.gameObjects[i].getId());
                        configs.add(tile.gameObjects[i].getConfig());
                    }
                }
                if (!ids.isEmpty()) {
                    gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
                    gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
                }
            }
        }
        if (exportOptions.exportWalls()) {
            if (tile.wall != null) {
                wallId = tile.wall.getId();
                wallConfig = tile.wall.getConfig();
            }
        }

        if (exportOptions.exportWallDecorations()) {
            if (tile.wallDecoration != null) {
                wallDecoId = tile.wallDecoration.getId();
                wallDecoConfig = tile.wallDecoration.getConfig();
            }
        }

        if (exportOptions.exportGroundDecorations()) {
            if (tile.groundDecoration != null) {
                groundDecoId = tile.groundDecoration.getId();
                groundDecoConfig = tile.groundDecoration.getConfig();
            }
        }
    }

    public SceneTileData(CopyOptions copyWindow, SceneTile tile, int tileX, int tileY) {
        if (copyWindow.copyGameObjects()) {
            if (tile.objectCount > 0) {
                List<Integer> ids = Lists.newArrayList();
                List<Integer> configs = Lists.newArrayList();
                for (int i = 0; i < tile.objectCount; i++) {
                    if (tile.gameObjects[i].getX() == tileX && tile.gameObjects[i].getY() == tileY) {
                        ids.add(tile.gameObjects[i].getId());
                        configs.add(tile.gameObjects[i].getConfig());
                    }
                }
                if (!ids.isEmpty()) {
                    gameObjectIds = ids.stream().mapToInt(i -> i).toArray();
                    gameObjectConfigs = configs.stream().mapToInt(i -> i).toArray();
                }
            }
        }
        if (copyWindow.copyWalls()) {
            if (tile.wall != null) {
                wallId = tile.wall.getId();
                wallConfig = tile.wall.getConfig();
            }
        }

        if (copyWindow.copyWallDecorations()) {
            if (tile.wallDecoration != null) {
                wallDecoId = tile.wallDecoration.getId();
                wallDecoConfig = tile.wallDecoration.getConfig();
            }
        }

        if (copyWindow.copyGroundDecorations()) {
            if (tile.groundDecoration != null) {
                groundDecoId = tile.groundDecoration.getId();
                groundDecoConfig = tile.groundDecoration.getConfig();
            }
        }
    }

    public Location getLocation() {
        return new Location(x, y, z);
    }
}
