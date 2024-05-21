package com.jagex.util;

import org.major.map.RenderFlags;

import java.util.BitSet;
import java.util.List;

public class BitFlag {

    private final BitSet flags = new BitSet();

    public BitFlag() {

    }

    public BitFlag(BitFlag toCopy, List<RenderFlags> exclude) {
        for (RenderFlags flag : RenderFlags.values()) {
            if (toCopy.flagged(flag) && !exclude.contains(flag)) {
				flag(flag);
			}
        }
    }

    public BitFlag(byte val) {
        for (RenderFlags flag : RenderFlags.values()) {
            if ((val & flag.getBit()) == flag.getBit()) {
				this.flag(flag);
			}
        }
    }

    public boolean flagged(RenderFlags flag) {
        return flags.get(flag.ordinal());
    }

    public BitFlag flag(RenderFlags flag) {
        flags.set(flag.ordinal(), true);
        return this;
    }

    public BitFlag reset() {
        flags.clear();
        return this;
    }

    public byte encode() {
        byte flag = 0;
        if (this.flagged(RenderFlags.BLOCKED_TILE)) {
            flag |= (byte) RenderFlags.BLOCKED_TILE.getBit();
        }
        if (this.flagged(RenderFlags.BRIDGE_TILE)) {
            flag |= (byte) RenderFlags.BRIDGE_TILE.getBit();
        }
        if (this.flagged(RenderFlags.FORCE_LOWEST_PLANE)) {
            flag |= (byte) RenderFlags.FORCE_LOWEST_PLANE.getBit();
        }
        if (this.flagged(RenderFlags.RENDER_ON_LOWER_Z)) {
            flag |= (byte) RenderFlags.RENDER_ON_LOWER_Z.getBit();
        }
        if (this.flagged(RenderFlags.DISABLE_RENDERING)) {
            flag |= (byte) RenderFlags.DISABLE_RENDERING.getBit();
        }
        return flag;
    }
}
