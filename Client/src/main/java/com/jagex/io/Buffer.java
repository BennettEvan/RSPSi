package com.jagex.io;

import com.displee.cache.index.archive.file.File;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Setter
@Getter
public final class Buffer {

    private static final int[] BIT_MASKS = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383,
            32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff,
            0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1};
    private static char[] CHARACTERS = {'\u20AC', '\0', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020', '\u2021',
            '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018', '\u2019', '\u201C',
            '\u201D', '\u2022', '\u2013', '\u2014', '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E',
            '\u0178'};
    public int position;
    private int bitPosition;
    private byte[] payload;

    public Buffer(ByteBuffer buffer) {
        this.payload = buffer.array();
        this.position = 0;
    }

    public Buffer(byte[] payload) {
        this.payload = payload;
        position = 0;
    }


    public Buffer(File payload) {
        this.payload = payload.getData();
        position = 0;
    }

    public byte readByte() {
        return payload[position++];
    }

    public int readInt() {
        position += 4;
        return ((payload[position - 4] & 0xff) << 24) + ((payload[position - 3] & 0xff) << 16)
                + ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
    }

    public int readShort() {
        position += 2;
        int value = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
        if (value > 32767) {
            value -= 0x10000;
        }
        return value;
    }

    public int readShort2() {
        position += 2;
        int value = ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
        if (value > 60000) {
            value -= 65535;
        }
        return value;
    }

    public int readSmart() {
        int value = payload[position] & 0xff;
        if (value < 128) {
			return readUByte() - 64;
		}
        return readUShort() - 49152;
    }

    public String readOSRSString() {
        StringBuilder bldr = new StringBuilder();
        int b;
        while ((b = payload[position++]) != 0) {
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

    public int readUByte() {
        return payload[position++] & 0xff;
    }

    public int readUShort() {
        position += 2;
        return ((payload[position - 2] & 0xff) << 8) + (payload[position - 1] & 0xff);
    }

    public int readUSmart() {
        int value = payload[position] & 0xff;
        if (value < 128) {
			return readUByte();
		}
        return readUShort() - 0x8000;
    }

    public int readUSmartInt() {
        int val = 0;
        int lastVal = 0;
        while ((lastVal = readUSmart()) == 32767) {
            val += 32767;
        }
        return val + lastVal;
    }

    public int readUTriByte() {
        position += 3;
        return ((payload[position - 3] & 0xff) << 16) + ((payload[position - 2] & 0xff) << 8)
                + (payload[position - 1] & 0xff);
    }

    public void writeByte(int i) {
        payload[position++] = (byte) i;
    }

    public void writeShort(int i) {
        payload[position++] = (byte) (i >> 8);
        payload[position++] = (byte) i;
    }

    public void writeUSmart(int value) {
        if (value < 128) {
            this.writeByte(value);
        } else {
            this.writeShort(0x8000 | value);
        }
    }

    public void writeUSmartInt(int value) {
        if (value > Short.MAX_VALUE) {
            writeIntSmart(value);
        } else {
            writeUSmart(value);
        }
    }

    public void writeIntSmart(int value) {
        Preconditions.checkArgument(value > 32767);
        Preconditions.checkArgument(value < (Integer.MAX_VALUE - 32767));
        int diff = value - 32767;
        writeShort(65535);
        writeUSmart(diff);
    }

    public void skip(int bytesToSkip) {
        position += bytesToSkip;
    }
}