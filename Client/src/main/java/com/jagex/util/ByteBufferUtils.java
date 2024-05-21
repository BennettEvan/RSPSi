package com.jagex.util;

import java.nio.ByteBuffer;

public final class ByteBufferUtils {

    private static final char[] CHARACTERS = {'\u20AC', '\0', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020', '\u2021',
            '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018', '\u2019', '\u201C',
            '\u201D', '\u2022', '\u2013', '\u2014', '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E',
            '\u0178'};

    private ByteBufferUtils() {

    }

    public static String getOSRSString(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while ((b = buf.get()) != 0) {
            if (b >= 127 && b < 160) {
                char curChar = CHARACTERS[b - 128];
                if (curChar == 0) {
                    curChar = 63;
                }

                bldr.append(curChar);
            } else {
                bldr.append((char) b);
            }
        }
        return bldr.toString();
    }

    public static int getUMedium(ByteBuffer buffer) {
        return (buffer.getShort() & 0xFFFF) << 8 | buffer.get() & 0xFF;
    }

    public static int getSmartInt(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) < 0) {
			return buffer.getInt() & 0x7fffffff;
		}
        return buffer.getShort() & 0xFFFF;
    }

    public static int getMedium(ByteBuffer buf) {
        return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
    }
}
