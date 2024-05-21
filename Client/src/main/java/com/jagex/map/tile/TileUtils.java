package com.jagex.map.tile;

public class TileUtils {
    public static final SimpleTile NON_WALKABLE = new SimpleTile(64183, 64183, 64183, 64183, -1, 64183, true, 0, false);
    public static final SimpleTile NON_WALKABLE_OTHER_HEIGHT = new SimpleTile(54183, 54183, 54183, 54183, -1, 54183, true, 0, false);
    public static final SimpleTile SELECTED_TILE = new SimpleTile(0xbc614d, 0xbc614d, 0xbc614d, 0xbc614d, -1, 0x0c3c69, true, 0, false);
    public static final SimpleTile BEING_SELECTED_TILE = new SimpleTile(49073, 49073, 49073, 49073, -1, 49073, true, 49073, false);
    public static final SimpleTile HIGHLIGHT_TILE = new SimpleTile(0xbc614d, 0xbc614d, 0xbc614d, 0xbc614d, -1, 0xFF00FF, true, 0, false);
    public static final SimpleTile HIDDEN_TILE = new SimpleTile(0xbc614d, 0xbc614d, 0xbc614d, 0xbc614d, -1, 0x00ffff, true, 0, false);
}